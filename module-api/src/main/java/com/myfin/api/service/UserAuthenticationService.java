package com.myfin.api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthenticationService extends UserDetailsService {

    /**
     * 유저 PK ID를 이용하여 DB를 조회한 뒤, <br>
     * UsernamePasswordAuthenticationToken 을 반환하는 메소드.
     * @param userId Authentication을 가져올 user의 PK ID
     * @return UsernamePasswordAuthenticationToken 로 객체화한 Authentication 인터페이스
     */
    Authentication getAuthentication(String userId);

}
