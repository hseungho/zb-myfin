package com.myfin.api.service;

import com.myfin.core.util.SeoulDate;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ATopServiceComponent 클래스는 Service 클래스들의 최상위 클래스로써, <br>
 * 다수의 Service 클래스에서 사용되는 기능들을 정의하도록 한 클래스이다. <br>
 * 클래스 앞에 `A` 접두사가 붙은 이유는 알파벳순으로 패키지 최상단에 위치하고자 함이다.
 */
public class ATopServiceComponent {

    /**
     * 파라미터가 null인지 확인.
     * @param args 확인할 Object 변수(들)
     * @return null이라면 true
     */
    protected boolean isNull(Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * String 파라미터가 null이 아니면서 blank가 아닌지 확인.
     * @param args 확인할 String 변수(들)
     * @return null이 아니면서 blank가 아니라면 true
     */
    protected boolean hasTexts(String... args) {
        for (String arg : args) {
            if (!StringUtils.hasText(arg)) {
                return false;
            }
        }
        return true;
    }

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

    /**
     * 두 파라미터가 일치하는지 확인
     * @param o1 확인할 파라미터 1
     * @param o2 확인할 파라미터 2
     * @return 두 파라미터가 일치하면 true
     */
    protected boolean isMatch(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    /**
     * 올바른 형식의 휴대폰번호인지 확인.
     * @param phoneNum 확인할 휴대폰번호
     * @return 올바른 형식이라면 true
     */
    protected boolean isValidPhoneNumPattern(String phoneNum) {
        String regex = "^010[.-]?(\\d{4})[.-]?(\\d{4})$";
        return Pattern.matches(regex, phoneNum);
    }

    /**
     * 올바르지 않은 형식의 휴대폰번호인지 확인.
     * @param phoneNum 확인할 휴대폰번호
     * @return 올바르지 않은 형식이라면 true
     */
    protected boolean isInvalidPhoneNumPattern(String phoneNum) {
        return !isValidPhoneNumPattern(phoneNum);
    }

    /**
     * 올바른 형식의 유저패스워드인지 확인. <br>
     * 1. 영문자 + 숫자 + 특수문자 조합의 8자리 이상의 문자열인지 <br>
     * 2. 유저패스워드에 유저아이디가 포함되지 않은지 <br>
     * 3. 유저패스워드에 공백이 포함되지 않은지 <br>
     * @param userId 유저아이디
     * @param password 유저패스워드
     * @return 올바른 형식이라면 true
     */
    protected boolean isValidPassword(String userId, String password) {
        String regex = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{8,100}$";
        return Pattern.matches(regex, password) && !password.contains(userId) && !password.contains(" ");
    }

    /**
     * 올바르지 않은 형식의 유저패스워드인지 확인. <br>
     * 1. 영문자 + 숫자 + 특수문자 조합의 8자리 이상의 문자열인지 <br>
     * 2. 유저패스워드에 유저아이디가 포함되지 않은지 <br>
     * 3. 유저패스워드에 공백이 포함되지 않은지 <br>
     * @param userId 유저아이디
     * @param password 유저패스워드
     * @return 올바르지 않은 형식이라면 true
     */
    protected boolean isInvalidPassword(String userId, String password) {
        return !isValidPassword(userId, password);
    }

    /**
     * 요청일자가 서버 현재일자보다 이후인지 확인.
     * @param date 요청일자
     * @return 요청일자가 서버 현재일자보다 이후라면 true
     */
    protected boolean isAfterThanNow(LocalDate date) {
        return date.isAfter(SeoulDate.now());
    }

    /**
     * 올바른 형식의 이메일주소인지 확인.
     * @param email 확인할 이메일주소
     * @return 올바른 형식이라면 true
     */
    protected boolean isValidEmailPattern(String email) {
        String regex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        return Pattern.matches(regex, email);
    }

    /**
     * 올바르지 않은 형식의 이메일주소인지 확인.
     * @param email 확인할 이메일주소
     * @return 올바르지 않은 형식이라면 true
     */
    protected boolean isInvalidEmailPattern(String email) {
        return !isValidEmailPattern(email);
    }

}
