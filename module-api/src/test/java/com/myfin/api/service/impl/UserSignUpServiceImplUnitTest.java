package com.myfin.api.service.impl;

import com.myfin.adapter.coolsms.SMSMessageComponent;
import com.myfin.api.dto.SignUp;
import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyIdentityResultDto;
import com.myfin.cache.entity.CacheVerifyCode;
import com.myfin.cache.repository.CacheVerifiedRepository;
import com.myfin.cache.repository.CacheVerifyCodeRepository;
import com.myfin.core.dto.UserDto;
import com.myfin.core.entity.User;
import com.myfin.core.entity.UserAddressVO;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.type.SexType;
import com.myfin.core.util.SeoulDate;
import com.myfin.core.util.SeoulDateTime;
import com.myfin.security.service.EncryptService;
import com.myfin.security.service.PasswordEncoderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceImplUnitTest {

    @InjectMocks
    private UserSignUpServiceImpl userSignUpService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheVerifyCodeRepository cacheVerifyCodeRepository;
    @Mock
    private SMSMessageComponent smsMessageComponent;
    @Mock
    private CacheVerifiedRepository cacheVerifiedRepository;
    @Mock
    private PasswordEncoderService passwordEncoderService;
    @Mock
    private EncryptService encryptService;

    @Test
    @DisplayName("아이디 사용가능여부 확인 - 사용가능")
    void test_checkUserIdAvailable_result_will_be_true() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        boolean result = userSignUpService.checkUserIdAvailable("user-id");
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
        boolean result = userSignUpService.checkUserIdAvailable("user-id");
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
                () -> userSignUpService.checkUserIdAvailable("")
        );
        BadRequestException ex_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.checkUserIdAvailable(null)
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
        given(cacheVerifyCodeRepository.findById(anyString()))
                .willReturn(Optional.empty());
        doNothing().when(smsMessageComponent).sendMessage(anyString(), anyString());
        given(cacheVerifyCodeRepository.save(any()))
                .willReturn(CacheVerifyCode.of("01012341234", "123456"));
        // when
        LocalDateTime result = userSignUpService.sendPhoneMessageForVerifyingIdentity("010-1234-1234");
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
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity(null)
        );
        BadRequestException ex_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("")
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
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("010-1234-12345")
        );
        BadRequestException ex_2 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("010-12345-1234")
        );
        BadRequestException ex_3 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("01-1234-12345")
        );
        BadRequestException ex_4 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("010123412345")
        );
        BadRequestException ex_5 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("010-123412345")
        );
        BadRequestException ex_6 = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.sendPhoneMessageForVerifyingIdentity("011234-12345")
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
        given(cacheVerifyCodeRepository.findById(anyString()))
                .willReturn(Optional.of(
                        CacheVerifyCode.of("01012341234", "123456")
                ));
        // when
        VerifyIdentityResultDto result = userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
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
        given(cacheVerifyCodeRepository.findById(anyString()))
                .willReturn(Optional.of(
                        CacheVerifyCode.of("01012341234", "654321")
                ));
        // when
        VerifyIdentityResultDto result = userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
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
        given(cacheVerifyCodeRepository.findById(anyString()))
                .willReturn(Optional.empty());
        // when
        VerifyIdentityResultDto result = userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
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
                () -> userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum(null).code("123456").build()));
        BadRequestException ex_phoneBlank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("").code("123456").build()));
        BadRequestException ex_phonePattern = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("010-01231-4123").code("123456").build()));
        BadRequestException ex_codeNull = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
                        .phoneNum("01012341234").code(null).build()));
        BadRequestException ex_codeBlank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.verifyIdentity(VerifyIdentity.Request.builder()
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

    @Test
    @DisplayName("회원가입")
    void test_signUp() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        given(userRepository.existsByPhoneNum(anyString()))
                .willReturn(false);
        given(cacheVerifiedRepository.existsById(anyString()))
                .willReturn(true);
        doNothing().when(cacheVerifiedRepository).deleteById(anyString());
        given(passwordEncoderService.encode(anyString()))
                .willReturn("encrypted_password");
        given(encryptService.encrypt(anyString()))
                .willReturn("encrypted_phoneNum");

        LocalDate today = SeoulDate.now();
        LocalDateTime now = SeoulDateTime.now();
        given(userRepository.save(any()))
                .willReturn(User.builder()
                        .id("uuid")
                        .userId("user-id")
                        .name("user-name")
                        .birthDate(today)
                        .sex(SexType.MALE)
                        .userAddress(UserAddressVO.of("zipcode", "address1", "address2"))
                        .phoneNum("encrypted_phoneNum")
                        .email("user-email")
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
        // when
        UserDto result = userSignUpService.signUp(SignUp.Request.builder()
                .userId("userid")
                .password("password1234!")
                .userName("username")
                .birthDate(today)
                .sex(false)
                .zipCode("zipcode")
                .address1("address1")
                .address2("address2")
                .phoneNum("010-1234-1234")
                .email("test@test.com")
                .build());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        // then
        verify(userRepository, times(1)).save(captor.capture());
        Assertions.assertNotNull(result);
        Assertions.assertEquals("uuid", result.getId());
        Assertions.assertEquals("user-id", result.getUserId());
        Assertions.assertEquals("user-name", result.getName());
        Assertions.assertEquals(today, result.getBirthDate());
        Assertions.assertEquals(SexType.MALE, result.getSex());
        Assertions.assertEquals("zipcode", result.getZipCode());
        Assertions.assertEquals("address1", result.getAddress1());
        Assertions.assertEquals("address2", result.getAddress2());
        Assertions.assertEquals("encrypted_phoneNum", result.getPhoneNum());
        Assertions.assertEquals("user-email", result.getEmail());
        Assertions.assertEquals(now, result.getCreatedAt());
        Assertions.assertEquals(now, result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("회원가입 - 실패 - 파라미터 불충분")
    void test_signUp_request_param_will_nullOrBlank() {
        // given
        // when
        // then
        BadRequestException ex_userId_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId(null).build()));
        BadRequestException ex_userId_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("").build()));
        BadRequestException ex_pw_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password(null).build()));
        BadRequestException ex_pw_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("").build()));
        BadRequestException ex_nm_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName(null).build()));
        BadRequestException ex_nm_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("").build()));
        BadRequestException ex_zc_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode(null).build()));
        BadRequestException ex_zc_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("").build()));
        BadRequestException ex_ad1_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("zc").address1(null).build()));
        BadRequestException ex_ad1_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("zc").address1("").build()));
        BadRequestException ex_pn_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("zc").address1("ad1").phoneNum(null).build()));
        BadRequestException ex_pn_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("zc").address1("ad1").phoneNum("").build()));
        BadRequestException ex_bd_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid").password("pw").userName("nm")
                        .zipCode("zc").address1("ad1").phoneNum("pn")
                        .birthDate(null).build()));
        Assertions.assertEquals(400, ex_userId_null.getHttpStatus());
        Assertions.assertEquals(400, ex_userId_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_pw_null.getHttpStatus());
        Assertions.assertEquals(400, ex_pw_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_nm_null.getHttpStatus());
        Assertions.assertEquals(400, ex_nm_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_zc_null.getHttpStatus());
        Assertions.assertEquals(400, ex_zc_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_ad1_null.getHttpStatus());
        Assertions.assertEquals(400, ex_ad1_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_pn_null.getHttpStatus());
        Assertions.assertEquals(400, ex_pn_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_bd_null.getHttpStatus());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_userId_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_userId_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_pw_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_pw_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_nm_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_nm_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_zc_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_zc_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_ad1_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_ad1_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_pn_null.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_pn_blank.getErrorMessage());
        Assertions.assertEquals("회원가입에 필요한 필수 정보를 모두 요청해주세요.", ex_bd_null.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 이미 존재하는 아이디")
    void test_signUp_when_already_exist_userId() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(true);
        // when
        // then
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test@test.com")
                        .build())
        );
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("이미 존재하는 아이디입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 비밀번호 형식 불충분")
    void test_signUp_when_invalid_password_pattern() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("pass1234")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test@test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("비밀번호는 영문자, 숫자, 특수문자를 조합하여 8자리 이상이어야 합니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 잘못된 생년월일 (현재 이후)")
    void test_signUp_when_invalid_birthDate() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now().plusDays(1L))
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test@test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("생년월일이 오늘보다 이후일 수는 없습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 휴대폰번호 형식 불충분")
    void test_signUp_when_invalid_phoneNum_pattern() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-01234-1234")
                        .email("test@test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("올바른 형식의 휴대폰번호를 입력해주세요.", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 이미 존재하는 휴대폰번호")
    void test_signUp_when_already_exist_phoneNum() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        given(userRepository.existsByPhoneNum(anyString()))
                .willReturn(true);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test@test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("이미 존재하는 휴대폰번호입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 이메일주소 형식 불충분")
    void test_signUp_when_invalid_email_pattern() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        given(userRepository.existsByPhoneNum(anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("올바른 형식의 이메일주소를 입력해주세요.", ex.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입 - 실패 - 아직 본인인증 안된 휴대폰번호")
    void test_signUp_when_did_not_verify_not_yet() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        given(userRepository.existsByPhoneNum(anyString()))
                .willReturn(false);
        given(cacheVerifiedRepository.existsById(anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> userSignUpService.signUp(SignUp.Request.builder()
                        .userId("userid")
                        .password("password1234!")
                        .userName("username")
                        .birthDate(SeoulDate.now())
                        .sex(false)
                        .zipCode("zipcode")
                        .address1("address1")
                        .address2("address2")
                        .phoneNum("010-1234-1234")
                        .email("test@test.com")
                        .build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("본인인증되지 않은 휴대폰번호입니다.", ex.getErrorMessage());
    }

}