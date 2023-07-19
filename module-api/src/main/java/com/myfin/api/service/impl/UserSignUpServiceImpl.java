package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyIdentityResultDto;
import com.myfin.api.service.ATopServiceComponent;
import com.myfin.api.service.UserSignUpService;
import com.myfin.cache.entity.CacheVerifyCode;
import com.myfin.cache.repository.CacheVerifyCodeRepository;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.Generator;
import com.myfin.core.util.SeoulDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSignUpServiceImpl extends ATopServiceComponent implements UserSignUpService {

    private final UserRepository userRepository;
    private final CacheVerifyCodeRepository cacheVerifyCodeRepository;

    private final SMSMessageComponent smsMessageComponent;

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

        final String phoneNum = requestedPhoneNum.replace("-", "");

        // 캐시 저장소에서 휴대폰번호로 조회 후 존재하면 삭제
        cacheVerifyCodeRepository.findByPhoneNum(phoneNum)
                .ifPresent(cacheVerifyCodeRepository::delete);

        // 인증코드 생성
        String verifyCode = Generator.generateVerifyCode();

        // 인증문자 발송
        smsMessageComponent.sendMessage(phoneNum, verifyCode);

        // 캐시 저장소 <휴대폰번호, 인증코드> 저장
        cacheVerifyCodeRepository.save(CacheVerifyCode.of(phoneNum, verifyCode));

        // 현재시간 반환
        return SeoulDateTime.now();
    }

    @Override
    public VerifyIdentityResultDto verifyIdentity(VerifyIdentity.Request request) {
        validateVerifyIdentity(request);

        final String phoneNum = request.getPhoneNum().replace("-", "");

        VerifyIdentityResultDto result = new VerifyIdentityResultDto();

        cacheVerifyCodeRepository.findByPhoneNum(phoneNum)
                .ifPresentOrElse(
                        it -> {
                            if (isMatch(it.getCode(), request.getCode())) {
                                result.setResult(true);
                                result.setMessage("인증되었습니다");
                                cacheVerifyCodeRepository.delete(it);
                            } else {
                                result.setResult(false);
                                result.setMessage("인증번호가 일치하지 않습니다");
                            }
                        },
                        () -> {
                            result.setResult(false);
                            result.setMessage("인증번호가 만료되었거나 인증문자를 요청하지 않았습니다. 다시 인증번호를 요청해주세요");
                        }
                );

        return result;
    }

    private void validateVerifyIdentity(VerifyIdentity.Request request) {
        if (hasNotTexts(request.getPhoneNum(), request.getCode())) {
            throw new BadRequestException("본인확인을 위한 모든 정보를 요청해주세요.");
        }
        validatePhoneNumberPattern(request.getPhoneNum());
    }

    private void validateCheckUserIdAvailableRequest(String userId) {
        if (hasNotTexts(userId)) {
            throw new BadRequestException("중복확인할 아이디를 입력해주세요.");
        }
    }

    private void validateSendPhoneMessageRequest(String phoneNum) {
        if (hasNotTexts(phoneNum)) {
            throw new BadRequestException("휴대폰번호를 입력해주세요.");
        }
        validatePhoneNumberPattern(phoneNum);
    }

    private void validatePhoneNumberPattern(String phoneNum) {
        String pattern = "^010[.-]?(\\d{4})[.-]?(\\d{4})$";
        if (!Pattern.matches(pattern, phoneNum)) {
            throw new BadRequestException("올바른 형식의 휴대폰번호를 입력해주세요.");
        }
    }

}
