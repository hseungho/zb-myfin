package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.api.dto.SignUp;
import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyIdentityResultDto;
import com.myfin.api.service.ATopServiceComponent;
import com.myfin.api.service.UserSignUpService;
import com.myfin.cache.entity.CacheVerified;
import com.myfin.cache.entity.CacheVerifyCode;
import com.myfin.cache.repository.CacheVerifiedRepository;
import com.myfin.cache.repository.CacheVerifyCodeRepository;
import com.myfin.core.dto.UserDto;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.Generator;
import com.myfin.core.util.SeoulDateTime;
import com.myfin.security.service.EncryptService;
import com.myfin.security.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSignUpServiceImpl extends ATopServiceComponent implements UserSignUpService {

    private final UserRepository userRepository;
    private final CacheVerifyCodeRepository cacheVerifyCodeRepository;
    private final CacheVerifiedRepository cacheVerifiedRepository;

    private final SMSMessageComponent smsMessageComponent;
    private final PasswordEncoderService passwordEncoderService;
    private final EncryptService encryptService;

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserIdAvailable(String userId) {
        validateCheckUserIdAvailableRequest(userId);
        return !userRepository.existsByUserId(userId);
    }

    @Override
    @Transactional
    public LocalDateTime sendPhoneMessageForVerifyingIdentity(String requestedPhoneNum) {
        // 요청 검증
        validateSendPhoneMessageRequest(requestedPhoneNum);

        final String phoneNum = convertPhoneNum(requestedPhoneNum);

        // 인증코드 캐시 저장소에서 휴대폰번호로 조회 후 존재하면 삭제
        cacheVerifyCodeRepository.findById(phoneNum)
                .ifPresent(cacheVerifyCodeRepository::delete);

        // 인증코드 생성
        String verifyCode = Generator.generateVerifyCode();

        // 인증문자 발송
        smsMessageComponent.sendMessage(phoneNum, verifyCode);

        // 인증코드 캐시 저장소에 <휴대폰번호, 인증코드> 저장
        cacheVerifyCodeRepository.save(CacheVerifyCode.of(phoneNum, verifyCode));

        // 현재시간 반환
        return SeoulDateTime.now();
    }

    @Override
    @Transactional
    public VerifyIdentityResultDto verifyIdentity(VerifyIdentity.Request request) {
        // 요청 검증
        validateVerifyIdentity(request);

        VerifyIdentityResultDto result = new VerifyIdentityResultDto();

        final String phoneNum = convertPhoneNum(request.getPhoneNum());

        // 인증코드 캐시 저장소에서 휴대폰번호로 조회
        cacheVerifyCodeRepository.findById(phoneNum)
                .ifPresentOrElse(
                        // 인증코드 캐시 저장소에 인증정보가 있을 경우
                        it -> {
                            if (isMatch(it.getCode(), request.getCode())) {
                                result.setResult(true);
                                result.setMessage("인증되었습니다");
                                cacheVerifyCodeRepository.delete(it);   // 인증코드 캐시 저장소에서 삭제
                                cacheVerifiedRepository.save(CacheVerified.of(it.getPhoneNum()));   // 인증확인 캐시 저장소에 저장
                            } else {
                                result.setResult(false);
                                result.setMessage("인증번호가 일치하지 않습니다");
                            }
                        },
                        // 캐시 저장소에 인증정보가 없을 경우
                        () -> {
                            result.setResult(false);
                            result.setMessage("인증번호가 만료되었거나 인증문자를 요청하지 않았습니다. 다시 인증번호를 요청해주세요");
                        }
                );

        return result;
    }

    @Override
    @Transactional
    public UserDto signUp(SignUp.Request request) {
        // 요청 검증
        validateSignUpRequest(request);

        // 본인인증 여부 검증
        final String phoneNum = convertPhoneNum(request.getPhoneNum());
        validateVerified(phoneNum);

        // 패스워드 암호화
        request.setPassword(passwordEncoderService.encode(request.getPassword()));

        // 휴대폰번호 암호화
        request.setPhoneNum(encryptService.encrypt(phoneNum));

        //  User 객체 생성 및 저장 -> DTO 반환
        return UserDto.fromEntity(
                userRepository.save(
                        User.create(
                                request.getUserId(),
                                request.getPassword(),
                                request.getUserName(),
                                request.getBirthDate(),
                                request.getSex(),
                                request.getZipCode(),
                                request.getAddress1(),
                                request.getAddress2(),
                                request.getPhoneNum(),
                                request.getEmail()
                        )
                )
        );
    }

    private void validateVerified(String phoneNum) {
        if (!cacheVerifiedRepository.existsById(phoneNum)) {
            // 회원가입을 위한 휴대폰번호가 아직 본인인증되지 않은 경우
            throw new BadRequestException("본인인증되지 않은 휴대폰번호입니다.");
        }
        cacheVerifiedRepository.deleteById(phoneNum);
    }

    private void validateSignUpRequest(SignUp.Request request) {
        if (hasNotTexts(request.getUserId(), request.getPassword(), request.getUserName(),
                request.getZipCode(), request.getAddress1(), request.getPhoneNum())
            || isNull(request.getBirthDate())) {
            // 회원가입을 위한 필수 정보를 요청하지 않은 경우
            throw new BadRequestException("회원가입에 필요한 필수 정보를 모두 요청해주세요.");
        }
        if (userRepository.existsByUserId(request.getUserId())) {
            // 회원가입을 위한 유저아이디가 이미 존재하는 경우
            throw new BadRequestException("이미 존재하는 아이디입니다");
        }
        if (isInvalidPassword(request.getUserId(), request.getPassword())) {
            // 회원가입을 위한 유저패스워드가 올바른 형식이 아닌 경우
            throw new BadRequestException("비밀번호는 영문자, 숫자, 특수문자를 조합하여 8자리 이상이어야 합니다");
        }
        if (isAfterThanNow(request.getBirthDate())) {
            // 회원가입을 위한 유저생년월일이 오늘보다 이후인 경우
            throw new BadRequestException("생년월일이 오늘보다 이후일 수는 없습니다");
        }
        if (isInvalidPhoneNumPattern(request.getPhoneNum())) {
            // 회원가입을 위한 휴대폰번호가 올바른 형식이 아닌 경우
            throw new BadRequestException("올바른 형식의 휴대폰번호를 입력해주세요.");
        }
        if (userRepository.existsByPhoneNum(request.getPhoneNum())) {
            // 회원가입을 위한 휴대폰번호가 이미 존재하는 경우
            throw new BadRequestException("이미 존재하는 휴대폰번호입니다");
        }
        if (hasTexts(request.getEmail()) && isInvalidEmailPattern(request.getEmail())) {
            // 회원가입을 위한 이메일 주소가 올바른 형식이 아닌 경우
            throw new BadRequestException("올바른 형식의 이메일주소를 입력해주세요.");
        }
    }

    private void validateVerifyIdentity(VerifyIdentity.Request request) {
        if (hasNotTexts(request.getPhoneNum(), request.getCode())) {
            // 본인확인을 위한 휴대폰번호나 인증코드를 요청하지 않은 경우
            throw new BadRequestException("본인확인을 위한 모든 정보를 요청해주세요.");
        }
        if (isInvalidPhoneNumPattern(request.getPhoneNum())) {
            // 휴대폰번호가 올바른 형식의 휴대폰번호가 아닌 경우
            throw new BadRequestException("올바른 형식의 휴대폰번호를 입력해주세요.");
        }
    }

    private void validateCheckUserIdAvailableRequest(String userId) {
        if (hasNotTexts(userId)) {
            // 중복확인을 위한 유저아이디를 요청하지 않은 경우
            throw new BadRequestException("중복확인할 아이디를 입력해주세요.");
        }
    }

    private void validateSendPhoneMessageRequest(String phoneNum) {
        if (hasNotTexts(phoneNum)) {
            // 본인인증 문자요청을 위한 휴대폰번호를 요청하지 않은 경우
            throw new BadRequestException("휴대폰번호를 입력해주세요.");
        }
        if (isInvalidPhoneNumPattern(phoneNum)) {
            // 휴대폰번호가 올바른 형식의 휴대폰번호가 아닌 경우
            throw new BadRequestException("올바른 형식의 휴대폰번호를 입력해주세요.");
        }
    }

}
