package com.myfin.api.service.impl;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.mock.TestSecurityHolder;
import com.myfin.core.dto.AccountDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.InternalServerException;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.type.UserType;
import com.myfin.core.util.SeoulDateTime;
import com.myfin.security.service.PasswordEncoderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplUnitTest {

    @InjectMocks private AccountServiceImpl accountService;

    @Mock private AccountRepository accountRepository;

    @Mock private PasswordEncoderService passwordEncoderService;

    @Test
    @DisplayName("계좌 생성 - 성공")
    void test_createAccount() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(UserType.ROLE_USER);
        LocalDateTime now = SeoulDateTime.now();

        given(accountRepository.existsByOwner(any()))
                .willReturn(false);
        given(accountRepository.existsByNumber(anyString()))
                .willReturn(false);
        given(passwordEncoderService.encode(anyString()))
                .willReturn("encrypted_password");
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .id(1L)
                        .number("account_number")
                        .password("encrypted_password")
                        .balance(10000L)
                        .createdAt(now)
                        .owner(user)
                        .build());
        // when
        AccountDto result = accountService.createAccount(CreateAccount.Request.builder()
                .accountPassword("1234").initialBalance(0L).build());
        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("account_number", result.getNumber());
        Assertions.assertEquals(10000L, result.getBalance());
        Assertions.assertEquals(now, result.getCreatedAt());
        Assertions.assertEquals(user.getId(), result.getOwner().getId());
        Assertions.assertEquals(user.getName(), result.getOwner().getName());
        Assertions.assertEquals(user.getPhoneNum(), result.getOwner().getPhoneNum());
        Assertions.assertEquals(user.getEmail(), result.getOwner().getEmail());
    }

    @Test
    @DisplayName("계좌 생성 - 실패 - 계좌비밀번호 미입력")
    void test_createAccount_when_accountPassword_null() {
        // given
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .initialBalance(1000L).build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("계좌 비밀번호를 입력해주세요", ex.getErrorMessage());
    }
    
    @Test
    @DisplayName("계좌 생성 - 실패 - 계좌비밀번호 4자리 X")
    void test_createAccount_failed_when_accountPassword_not_4_length() {
        // given
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .accountPassword("12123123").build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("계좌 비밀번호는 연속으로 중복되지 않는 4자리 숫자여야 합니다", ex.getErrorMessage());
    }
    
    @Test
    @DisplayName("계좌 생성 - 실패 - 계좌비밀번호 NaN")
    void test_createAccount_failed_when_accountPassword_is_NaN() {
        // given
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .accountPassword("pass").build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("계좌 비밀번호는 연속으로 중복되지 않는 4자리 숫자여야 합니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 생성 - 실패 - 계좌비밀번호 연속 및 중복")
    void test_createAccount_failed_when_accountPassword_duplicated_number_in_row() {
        // given
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .accountPassword("1123").build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("계좌 비밀번호는 연속으로 중복되지 않는 4자리 숫자여야 합니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 생성 - 실패 - 유저 계좌 이미 보유")
    void test_createAccount_failed_when_user_has_account() {
        // given
        TestSecurityHolder.setSecurityHolderUser(UserType.ROLE_USER);
        given(accountRepository.existsByOwner(any()))
                .willReturn(true);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .accountPassword("1234").build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("이미 계좌를 보유하고 있습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 생성 - 실패 - 계좌번호 생성 실패")
    void test_createAccount_failed_when_fail_accountNumber_generating() {
        // given
        TestSecurityHolder.setSecurityHolderUser(UserType.ROLE_USER);
        given(accountRepository.existsByOwner(any()))
                .willReturn(false);
        given(accountRepository.existsByNumber(anyString()))
                .willReturn(true);
        // when
        InternalServerException ex = Assertions.assertThrows(
                InternalServerException.class,
                () -> accountService.createAccount(CreateAccount.Request.builder()
                        .accountPassword("1234").build())
        );
        // then
        Assertions.assertEquals(500, ex.getHttpStatus());
        Assertions.assertEquals("계좌번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요", ex.getErrorMessage());
    }

}