package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.cache.entity.CacheVerifyCode;
import com.myfin.cache.repository.CacheVerifyCodeRepository;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UserCheckServiceImplUnitTest {

    @InjectMocks
    private UserCheckServiceImpl userCheckService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheVerifyCodeRepository cacheVerifyCodeRepository;
    @Mock
    private SMSMessageComponent smsMessageComponent;


    @Test
    @DisplayName("아이디 사용가능여부 확인 - 사용가능")
    void test_checkUserIdAvailable_result_will_be_true() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        boolean result = userCheckService.checkUserIdAvailable("user-id");
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("아이디 사용가능여부 확인 - 사용불가")
    void test_checkUserIdAvailable_result_will_be_false() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(true);
        // when
        boolean result = userCheckService.checkUserIdAvailable("user-id");
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("아이디 사용가능여부 확인 - blank & null")
    void test_checkUserIdAvailable_request_is_blank() {
        // given
        // when
        BadRequestException ex_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.checkUserIdAvailable("")
        );
        BadRequestException ex_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.checkUserIdAvailable(null)
        );
        // then
        Assertions.assertEquals(400, ex_blank.getHttpStatus());
        Assertions.assertEquals("중복확인할 아이디를 입력해주세요.", ex_blank.getErrorMessage());
        Assertions.assertEquals(400, ex_null.getHttpStatus());
        Assertions.assertEquals("중복확인할 아이디를 입력해주세요.", ex_null.getErrorMessage());
    }

    @Test
    @DisplayName("휴대폰 본인인증 문자요청")
    void test_sendPhoneMessageForVerifyingIdentity_success() {
        // given
        given(cacheVerifyCodeRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.empty());
        doNothing().when(smsMessageComponent).sendMessage(anyString(), anyString());
        given(cacheVerifyCodeRepository.save(any()))
                .willReturn(CacheVerifyCode.of("01012341234", "123456"));
        // when
        LocalDateTime result = userCheckService.sendPhoneMessageForVerifyingIdentity("010-1234-1234");
        // then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("휴대폰 본인인증 문자요청 - 휴대폰번호 null & blank")
    void test_sendPhoneMessageForVerifyingIdentity_phoneNum_will_be_nullOrBlank() {
        // given
        // when
        BadRequestException ex_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity(null)
        );
        BadRequestException ex_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("")
        );
        // then
        Assertions.assertEquals(400, ex_blank.getHttpStatus());
        Assertions.assertEquals("휴대폰번호를 입력해주세요.", ex_blank.getErrorMessage());
        Assertions.assertEquals(400, ex_null.getHttpStatus());
        Assertions.assertEquals("휴대폰번호를 입력해주세요.", ex_null.getErrorMessage());
    }

    @Test
    @DisplayName("휴대폰 본인인증 문자요청 - 휴대폰번호 형식 오류")
    void test_sendPhoneMessageForVerifyingIdentity_phoneNum_will_be_invalid_pattern() {
        // given
        // when
        BadRequestException ex_1 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("010-1234-12345")
        );
        BadRequestException ex_2 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("010-12345-1234")
        );
        BadRequestException ex_3 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("01-1234-12345")
        );
        BadRequestException ex_4 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("010123412345")
        );
        BadRequestException ex_5 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("010-123412345")
        );
        BadRequestException ex_6 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.sendPhoneMessageForVerifyingIdentity("011234-12345")
        );
        // then
        Assertions.assertEquals(400, ex_1.getHttpStatus());
        Assertions.assertEquals(400, ex_2.getHttpStatus());
        Assertions.assertEquals(400, ex_3.getHttpStatus());
        Assertions.assertEquals(400, ex_4.getHttpStatus());
        Assertions.assertEquals(400, ex_5.getHttpStatus());
        Assertions.assertEquals(400, ex_6.getHttpStatus());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_1.getErrorMessage());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_2.getErrorMessage());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_3.getErrorMessage());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_4.getErrorMessage());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_5.getErrorMessage());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex_6.getErrorMessage());
    }

}