package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.api.service.ATopServiceComponent;
import com.myfin.api.service.UserCheckService;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.Generator;
import com.myfin.core.util.SeoulDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCheckServiceImpl extends ATopServiceComponent implements UserCheckService {

    private final UserRepository userRepository;

    private final SMSMessageComponent smsMessageComponent;

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserIdAvailable(String userId) {
        if (hasNotTexts(userId)) {
            throw new BadRequestException("중복확인할 아이디를 입력해주세요.");
        }
        return !userRepository.existsByUserId(userId);
    }

    @Override
    @Transactional
    public LocalDateTime sendPhoneMessageForVerifyingIdentity(String phoneNum) {
        // 요청 검증

        // 캐시 저장소에서 휴대폰번호로 조회 후 존재하면 삭제

        // 인증코드 생성
        String verifyCode = Generator.generateVerifyCode();

        // 인증문자 발송
        smsMessageComponent.sendMessage(phoneNum, verifyCode);

        // 캐시 저장소 <휴대폰번호, 인증코드> 저장

        // 현재시간 반환
        return SeoulDateTime.now();
    }

}
