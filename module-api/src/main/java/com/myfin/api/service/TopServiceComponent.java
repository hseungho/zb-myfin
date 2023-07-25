package com.myfin.api.service;

import com.myfin.core.entity.User;
import com.myfin.core.util.SeoulDate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ATopServiceComponent 클래스는 Service 클래스들의 최상위 클래스로써, <br>
 * 다수의 Service 클래스에서 사용되는 기능들을 정의하도록 한 클래스이다.
 */
public class TopServiceComponent {

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

    /**
     * 올바르지 않은 형식의 계정 비밀번호인지 확인.
     * @param password
     * @return
     */
    protected boolean isInvalidAccountPassword(String password) {
        if (password.length() != 4)
            return true;
        if (!isNaN(password))
            return true;
        if (isExistDuplicateNumbersInRow(password))
            return true;

        return false;
    }

    /**
     * String 파라미터 중에 연속되어 중복된 문자가 있는지 확인.
     * @param number 확인할 String 변수
     * @return 중복된 문자가 존재하다면 true
     */
    private boolean isExistDuplicateNumbersInRow(String number) {
        for (int i = 0; i < number.length() - 1; i++) {
            char c1 = number.charAt(i);
            char c2 = number.charAt(i + 1);
            if (c1 == c2) {
                return true;
            }
        }
        return false;
    }

    /**
     * String 파라미터가 숫자인지 아닌지 확인.
     * @param number 확인할 String 변수
     * @return 올바른 숫자라면 true
     */
    protected boolean isNaN(String number) {
        boolean flag = true;
        if (number == null || "".equals(number))
            return false;

        int size = number.length();
        int st_no= 0;

        if (number.charAt(0) == 45)
            st_no = 1;

        for (int i=st_no; i<size; ++i) {
            if (!(48<=((int)number.charAt(i)) && 57>=((int)number.charAt(i)))) {
                flag = false;
                break;
            }
        }

        return flag;
    }

    /**
     * 휴대폰번호 중 '-'가 존재하다면 이를 제거하여 숫자로만 <br>
     * 구성된 휴대폰번호를 반환.
     * @param phoneNum '-'를 제거할 휴대폰번호
     * @return 휴대폰번호
     */
    protected String convertPhoneNum(String phoneNum) {
        return phoneNum.strip().replace("-", "");
    }

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
    private String loginUserId() {
        return ((User)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUserId();
    }

}
