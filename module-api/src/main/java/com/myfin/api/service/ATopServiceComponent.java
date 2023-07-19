package com.myfin.api.service;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * ATopServiceComponent 클래스는 Service 클래스들의 최상위 클래스로써, <br>
 * 다수의 Service 클래스에서 사용되는 기능들을 정의하도록 한 클래스이다. <br>
 * 클래스 앞에 `A` 접두사가 붙은 이유는 알파벳순으로 패키지 최상단에 위치하고자 함이다.
 */
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

    protected boolean isMatch(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

}
