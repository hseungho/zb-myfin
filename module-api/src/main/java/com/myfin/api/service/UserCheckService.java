package com.myfin.api.service;

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

}
