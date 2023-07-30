package com.myfin.api.service;

import com.myfin.core.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * TopServiceComponent 클래스는 Service 클래스들의 최상위 클래스로써, <br>
 * 다수의 Service 클래스에서 사용되는 기능들을 정의하도록 한 클래스이다.
 */
public class TopServiceComponent {


    /**
     * 로그인 유저의 `User` 객체를 반환.
     * @return 로그인 유저의 `User` 객체
     */
    protected User loginUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 로그인 유저의 PK ID를 반환.
     * @return 로그인 유저의 PK ID
     */
    protected String loginId() {
        return this.loginUser().getId();
    }

}
