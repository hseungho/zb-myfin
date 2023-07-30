package com.myfin.core.util;

import com.myfin.core.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil() {}

    /**
     * 로그인 유저의 `User` 객체를 반환.
     * @return 로그인 유저의 `User` 객체
     */
    public static User loginUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 로그인 유저의 PK ID를 반환.
     * @return 로그인 유저의 PK ID
     */
    public static String loginId() {
        return loginUser().getId();
    }


}
