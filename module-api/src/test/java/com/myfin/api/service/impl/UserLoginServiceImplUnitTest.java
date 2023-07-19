package com.myfin.api.service.impl;

import com.myfin.api.dto.TokenDto;
import com.myfin.core.entity.User;
import com.myfin.core.entity.UserAddressVO;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.exception.impl.UnauthorizedException;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.type.SexType;
import com.myfin.core.type.UserType;
import com.myfin.core.util.SeoulDate;
import com.myfin.core.util.SeoulDateTime;
import com.myfin.security.jwt.JwtComponent;
import com.myfin.security.service.PasswordEncoderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceImplUnitTest {

    @InjectMocks
    private UserLoginServiceImpl userLoginService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoderService passwordEncoderService;
    @Mock
    private JwtComponent jwtComponent;

    @Test
    @DisplayName("로그인 성공")
    void test_login() {
        // given
        given(userRepository.findByUserId(anyString()))
                .willReturn(Optional.of(
                        User.builder()
                                .id("uuid")
                                .userId("user_id")
                                .password("user_pw")
                                .name("user_name")
                                .birthDate(SeoulDate.now())
                                .sex(SexType.MALE)
                                .userAddress(UserAddressVO.of("zipcode", "address1", "address2"))
                                .phoneNum("user_phoneNum")
                                .email("user_email")
                                .type(UserType.ROLE_USER)
                                .build()
                ));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(false);
        given(jwtComponent.generateAccessToken(anyString(), any()))
                .willReturn("access_token");
        given(jwtComponent.generateRefreshToken(anyString(), any()))
                .willReturn("refresh_token");
        // when
        TokenDto result = userLoginService.login("user_id", "user_pw");
        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("access_token", result.getAccessToken());
        Assertions.assertEquals("refresh_token", result.getRefreshToken());
        Assertions.assertNotNull(result.getLastLoggedInAt());
    }

    @Test
    @DisplayName("로그인 실패 - 파라미터 불충분")
    void test_login_when_parameter_will_be_nullOrBlank() {
        // given
        // when
        // then
        BadRequestException ex_nulls = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login(null, null)
        );
        BadRequestException ex_blanks = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login("", "")
        );
        BadRequestException ex_pw_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login("user_id", null)
        );
        BadRequestException ex_pw_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login("user_id", "")
        );
        BadRequestException ex_id_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login(null, "user_pw")
        );
        BadRequestException ex_id_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userLoginService.login("", "user_pw")
        );
        Assertions.assertEquals(400, ex_nulls.getHttpStatus());
        Assertions.assertEquals(400, ex_blanks.getHttpStatus());
        Assertions.assertEquals(400, ex_pw_null.getHttpStatus());
        Assertions.assertEquals(400, ex_pw_blank.getHttpStatus());
        Assertions.assertEquals(400, ex_id_null.getHttpStatus());
        Assertions.assertEquals(400, ex_id_blank.getHttpStatus());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_nulls.getErrorMessage());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_blanks.getErrorMessage());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_pw_null.getErrorMessage());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_pw_blank.getErrorMessage());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_id_null.getErrorMessage());
        Assertions.assertEquals("아이디와 비밀번호를 모두 입력해주세요", ex_id_blank.getErrorMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void test_login_when_mismatch_password() {
        // given
        given(userRepository.findByUserId(anyString()))
                .willReturn(Optional.of(
                        User.builder()
                                .id("uuid")
                                .userId("user_id")
                                .password("user_pw")
                                .name("user_name")
                                .birthDate(SeoulDate.now())
                                .sex(SexType.MALE)
                                .userAddress(UserAddressVO.of("zipcode", "address1", "address2"))
                                .phoneNum("user_phoneNum")
                                .email("user_email")
                                .type(UserType.ROLE_USER)
                                .build()
                ));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(true);
        // when
        // then
        UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> userLoginService.login("user_id", "user_pw")
        );
        Assertions.assertEquals(401, ex.getHttpStatus());
        Assertions.assertEquals("잘못된 비밀번호입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 이미 탈퇴된 유저")
    void test_login_when_already_resigned() {
        // given
        given(userRepository.findByUserId(anyString()))
                .willReturn(Optional.of(
                        User.builder()
                                .id("uuid")
                                .userId("user_id")
                                .password("user_pw")
                                .name("user_name")
                                .birthDate(SeoulDate.now())
                                .sex(SexType.MALE)
                                .userAddress(UserAddressVO.of("zipcode", "address1", "address2"))
                                .phoneNum("user_phoneNum")
                                .email("user_email")
                                .type(UserType.ROLE_USER)
                                .deletedAt(SeoulDateTime.now())
                                .build()
                ));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(false);
        // when
        // then
        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class,
                () -> userLoginService.login("user_id", "user_pw")
        );
        Assertions.assertEquals(404, ex.getHttpStatus());
        Assertions.assertEquals("이미 탈퇴된 유저입니다", ex.getErrorMessage());
    }

}