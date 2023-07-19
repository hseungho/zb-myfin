package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyIdentityResultDto;
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

    @Test
    @DisplayName("휴대폰 본인인증 검증")
    void test_verifyIdentity() {
        // given
        given(cacheVerifyCodeRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.of(
                        CacheVerifyCode.of("01012341234", "123456")
                ));
        // when
        VerifyIdentityResultDto result = userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                .phoneNum("01012341234").code("123456").build());
        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isResult());
        Assertions.assertEquals("인증되었습니다", result.getMessage());
    }

    @Test
    @DisplayName("휴대폰 본인인증 검증 - 실패 - 코드 불일치")
    void test_verifyIdentity_code_will_be_wrong() {
        // given
        given(cacheVerifyCodeRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.of(
                        CacheVerifyCode.of("01012341234", "654321")
                ));
        // when
        VerifyIdentityResultDto result = userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                .phoneNum("01012341234").code("123456").build());
        // then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isResult());
        Assertions.assertEquals("인증번호가 일치하지 않습니다", result.getMessage());
    }
    
    @Test
    @DisplayName("휴대폰 본인인증 검증 - 실패 - 코드 만료")
    void test_verifyIdentity_code_will_be_expired() {
        // given
        given(cacheVerifyCodeRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.empty());
        // when
        VerifyIdentityResultDto result = userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                .phoneNum("01012341234").code("123456").build());
        // then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isResult());
        Assertions.assertEquals("인증번호가 만료되었거나 인증문자를 요청하지 않았습니다. 다시 인증번호를 요청해주세요", result.getMessage());
    }

    @Test
    @DisplayName("휴대폰 본인인증 검증 - 실패 - 파라미터 불충분")
    void test_verify_phone_will_nullOrBlank() {
        // given
        // when
        BadRequestException ex_phoneNull = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum(null).code("123456").build()));
        BadRequestException ex_phoneBlank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("").code("123456").build()));
        BadRequestException ex_phonePattern = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("010-01231-4123").code("123456").build()));
        BadRequestException ex_codeNull = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("01012341234").code(null).build()));
        BadRequestException ex_codeBlank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("01012341234").code("").build()));
        // then
        Assertions.assertEquals(ex_phoneNull.getHttpStatus(), 400);
        Assertions.assertEquals(ex_phoneBlank.getHttpStatus(), 400);
        Assertions.assertEquals(ex_phonePattern.getHttpStatus(), 400);
        Assertions.assertEquals(ex_codeNull.getHttpStatus(), 400);
        Assertions.assertEquals(ex_codeBlank.getHttpStatus(), 400);
        Assertions.assertEquals(ex_phoneNull.getMessage(), "본인확인을 위한 모든 정보를 요청해주세요.");
        Assertions.assertEquals(ex_phoneBlank.getMessage(), "본인확인을 위한 모든 정보를 요청해주세요.");
        Assertions.assertEquals(ex_codeNull.getMessage(), "본인확인을 위한 모든 정보를 요청해주세요.");
        Assertions.assertEquals(ex_codeBlank.getMessage(), "본인확인을 위한 모든 정보를 요청해주세요.");
        Assertions.assertEquals(ex_phonePattern.getMessage(), "올바른 형식의 휴대폰번호를 입력해주세요.");
    }

}