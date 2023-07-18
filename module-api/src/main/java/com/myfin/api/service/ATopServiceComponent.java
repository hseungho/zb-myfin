package com.myfin.api.service;

import org.springframework.util.StringUtils;

public class ATopServiceComponent {

    /**
     * String 파라미터가 null 또는 blank 인지 확인
     * @param args 확인할 String 변수(들)
     * @return null 또는 blank라면 true
     */
    protected boolean hasNotTexts(String... args) {
        for (String arg : args) {
            if (!StringUtils.hasText(arg)) {
                return true;
            }
        }
        return false;
    }

}
