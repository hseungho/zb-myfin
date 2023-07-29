package com.myfin.api.service.impl;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.dto.DeleteAccount;
import com.myfin.api.dto.FindMyAccount;
import com.myfin.api.mock.MockFactory;
import com.myfin.api.mock.TestSecurityHolder;
import com.myfin.core.dto.AccountDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.ForbiddenException;
import com.myfin.core.exception.impl.InternalServerException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplUnitTest {

    @InjectMocks private AccountServiceImpl accountService;

    @Mock private AccountRepository accountRepository;

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoderService passwordEncoderService;

    @Test
    @DisplayName("계좌 생성 - 성공")
    void test_createAccount() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
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
        assertNotNull(result);
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
        TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
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
        TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
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

    @Test
    @DisplayName("계좌 삭제 - 성공")
    void test_deleteAccount_success() {
        // given
        LocalDateTime now = SeoulDateTime.now();

        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
        MockFactory.mock_account(user, 0L, now, null, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(false);
        // when
        AccountDto result = accountService.deleteAccount(DeleteAccount.Request.builder()
                .accountNumber("account_number")
                .accountPassword("1234").build());
        // then
        assertNotNull(result);
        Assertions.assertEquals("account_number", result.getNumber());
        Assertions.assertEquals(now, result.getCreatedAt());
        assertNotNull(result.getDeletedAt());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 - 계좌번호 및 계좌비밀번호 미입력")
    void test_deleteAccount_failed_when_accountNumber_and_accountPassword_null() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(
                MockFactory.mock_user(UserType.ROLE_USER, null, null, null)
        );
        MockFactory.mock_account(user, 0L, null, null, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        BadRequestException ex_nmb_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountPassword("1234").build())
        );
        BadRequestException ex_pw_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountNumber("account_number").build())
        );
        // then
        Assertions.assertEquals(400, ex_nmb_null.getHttpStatus());
        Assertions.assertEquals(400, ex_pw_null.getHttpStatus());
        Assertions.assertEquals("계좌번호와 계좌비밀번호 모두 입력해주세요", ex_nmb_null.getErrorMessage());
        Assertions.assertEquals("계좌번호와 계좌비밀번호 모두 입력해주세요", ex_pw_null.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 - 계좌번호 불일치")
    void test_deleteAccount_failed_when_mismatch_accountNumber() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(
                MockFactory.mock_user(UserType.ROLE_USER, null, null, null)
        );
        MockFactory.mock_account(user, 0L, null, null, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        ForbiddenException ex = Assertions.assertThrows(
                ForbiddenException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountNumber("wrong_account_number").accountPassword("1234").build())
        );
        // then
        Assertions.assertEquals(403, ex.getHttpStatus());
        Assertions.assertEquals("해당 계좌번호는 유저님의 계좌번호가 아닙니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 - 이미 삭제된 계좌")
    void test_deleteAccount_failed_when_already_deleted_account() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(
                MockFactory.mock_user(UserType.ROLE_USER, null, null, null)
        );
        MockFactory.mock_account(user, 0L, null, null, SeoulDateTime.now());
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountNumber("account_number").accountPassword("1234").build())
        );
        // then
        Assertions.assertEquals(404, ex.getHttpStatus());
        Assertions.assertEquals("이미 삭제된 계좌입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 - 계좌비밀번호 불일치")
    void test_deleteAccount_failed_when_mismatch_accountPassword() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(
                MockFactory.mock_user(UserType.ROLE_USER, null, null, null)
        );
        MockFactory.mock_account(user, 0L, null, null, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(true);
        // when
        ForbiddenException ex = Assertions.assertThrows(
                ForbiddenException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountNumber("account_number").accountPassword("4321").build())
        );
        // then
        Assertions.assertEquals(403, ex.getHttpStatus());
        Assertions.assertEquals("계좌비밀번호가 일치하지 않습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 - 계좌 잔액 보유")
    void test_deleteAccount_failed_when_balance_existed() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(
                MockFactory.mock_user(UserType.ROLE_USER, null, null, null)
        );
        MockFactory.mock_account(user, 10000L, null, null, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(false);
        // when
        BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> accountService.deleteAccount(DeleteAccount.Request.builder()
                        .accountNumber("account_number").accountPassword("1234").build())
        );
        // then
        Assertions.assertEquals(400, ex.getHttpStatus());
        Assertions.assertEquals("계좌에 잔액이 존재한 경우에는 계좌를 삭제할 수 없습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 조회 - 성공")
    void test_findMyAccount_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        Account account = MockFactory.mock_account(user, 10000L, now, now, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(false);
        // when
        AccountDto result = accountService.findMyAccount(FindMyAccount.Request.builder()
                .accountNumber(account.getNumber()).accountPassword("ap").build());
        // then
        assertNotNull(result);
        assertEquals(account.getId(), result.getId());
        assertEquals(account.getNumber(), result.getNumber());
        assertEquals(account.getBalance(), result.getBalance());
        assertEquals(account.getCreatedAt(), result.getCreatedAt());
        assertEquals(account.getUpdatedAt(), result.getUpdatedAt());
        assertNull(result.getDeletedAt());
        assertEquals(user.getId(), result.getOwner().getId());
    }

    @Test
    @DisplayName("계좌 조회 - 실패 - 계좌번호 및 계좌비밀번호 미입력")
    void test_findMyAccount_failed_when_hasNotAccNumOrAccPassword() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        BadRequestException ex_an_null = assertThrows(
                BadRequestException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber(null).accountPassword("ap").build())
        );
        BadRequestException ex_ap_null = assertThrows(
                BadRequestException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber("an").accountPassword(null).build())
        );
        // then
        assertEquals(400, ex_an_null.getHttpStatus());
        assertEquals(400, ex_ap_null.getHttpStatus());
        assertEquals("계좌번호와 계좌비밀번호 모두 입력해주세요", ex_an_null.getErrorMessage());
        assertEquals("계좌번호와 계좌비밀번호 모두 입력해주세요", ex_ap_null.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 조회 - 실패 - 계좌 미보유")
    void test_findMyAccount_failed_when_hasNotAccount() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber("ap").accountPassword("1234").build())
        );
        // then
        assertEquals(404, ex.getHttpStatus());
        assertEquals("계좌를 보유하고 있지 않습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 조회 - 실패 - 이미 삭제된 계좌")
    void test_findMyAccount_failed_when_alreadyDeletedAccount() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        MockFactory.mock_account(user, 10000L, now, now, now);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber("an").accountPassword("ap").build())
        );
        // then
        assertEquals(404, ex.getHttpStatus());
        assertEquals("이미 삭제된 계좌입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 조회 - 실패 - 계좌번호 불일치")
    void test_findMyAccount_failed_when_mismatchAccNumber() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        MockFactory.mock_account(user, 10000L, now, now, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        // when
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber("wrong_an").accountPassword("ap").build())
        );
        // then
        assertEquals(403, ex.getHttpStatus());
        assertEquals("계좌번호가 일치하지 않습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 조회 - 실패 - 계좌비밀번호 불일치")
    void test_findMyAccount_failed_when_mismatchAccPassword() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        Account account = MockFactory.mock_account(user, 10000L, now, now, null);
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        given(passwordEncoderService.mismatch(anyString(), anyString()))
                .willReturn(true);
        // when
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> accountService.findMyAccount(FindMyAccount.Request.builder()
                        .accountNumber(account.getNumber()).accountPassword("ap").build())
        );
        // then
        assertEquals(403, ex.getHttpStatus());
        assertEquals("계좌비밀번호가 일치하지 않습니다", ex.getErrorMessage());
    }
}