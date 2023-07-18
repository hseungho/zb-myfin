package com.myfin.api.service;

import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyIdentityResultDto;

import java.time.LocalDateTime;

public interface UserCheckService {

    /**
     * 아이디 사용가능 여부 확인.
     * @param userId 확인할 유저 아이디
     * @return 사용가능하면 true
     */
    boolean checkUserIdAvailable(String userId);

    /**
     * 휴대폰 본인인증 문자 요청.
     * @param phoneNum 본인인증 문자 요청할 휴대폰번호
     * @return 문자 요청일시
     */
    LocalDateTime sendPhoneMessageForVerifyingIdentity(String phoneNum);

    /**
     * 휴대폰 본인인증 검증.
     * @param request 본인인증 검증 요청 DTO 클래스
     * @return 본인인증 검증 DTO 클래스
     */
    VerifyIdentityResultDto verifyIdentity(VerifyIdentity.Request request);
}
