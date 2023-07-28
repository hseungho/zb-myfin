package com.myfin.api.service.impl;

import com.myfin.api.dto.Deposit;
import com.myfin.api.mock.MockFactory;
import com.myfin.api.mock.TestSecurityHolder;
import com.myfin.core.dto.TransactionDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.Transaction;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.ForbiddenException;
import com.myfin.core.exception.impl.InternalServerException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.repository.TransactionRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.type.TransactionType;
import com.myfin.core.type.UserType;
import com.myfin.core.util.SeoulDateTime;
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
class TransactionServiceImplUnitTest {

    @InjectMocks private TransactionServiceImpl transactionService;

    @Mock private UserRepository userRepository;

    @Mock private TransactionRepository transactionRepository;

    @Test
    @DisplayName("계좌 입금 - 성공")
    void test_deposit_success() {
        // given
        LocalDateTime now = SeoulDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
        Account account = MockFactory.mock_account(user, 0L, now, now, null);
        Transaction transaction = MockFactory.mock_transaction(account, 1000L, account.getNumber(), TransactionType.DEPOSIT, now);
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        given(transactionRepository.existsByNumber(anyString()))
                .willReturn(false);
        given(transactionRepository.save(any()))
                .willReturn(transaction);
        // when
        TransactionDto result = transactionService.deposit(Deposit.Request.builder()
                .accountNumber(account.getNumber()).amount(1000L).build());
        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("txn_number", result.getNumber());
        assertEquals(1000L, result.getAmount());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(account.getNumber(), result.getRecipientAccountNumber());
        assertEquals(now, result.getTradedAt());
        assertEquals(account.getId(), result.getAccount().getId());
        assertEquals(account.getNumber(), result.getAccount().getNumber());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 유저 조회 실패")
    void test_deposit_failed_when_userNotFound() {
        // given
        TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        // when
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> transactionService.deposit(Deposit.Request.builder().build())
        );
        // then
        assertEquals(404, ex.getHttpStatus());
        assertEquals("존재하지 않는 유저입니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 계좌번호 없음")
    void test_deposit_failed_when_hasNotAccountNumber() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        // when
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> transactionService.deposit(Deposit.Request.builder().accountNumber(null).build())
        );
        // then
        assertEquals(400, ex.getHttpStatus());
        assertEquals("계좌번호를 입력해주세요", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 입금액 0원 이하")
    void test_deposit_failed_when_amountIsLessThanEqualsToZero() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        // when
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> transactionService.deposit(Deposit.Request.builder()
                        .accountNumber("12341234").amount(0L).build())
        );
        // then
        assertEquals(400, ex.getHttpStatus());
        assertEquals("입금액을 1원 이상 입력해주세요", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 유저 보유계좌 없음")
    void test_deposit_failed_when_accountIsNull() {
        // given
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER));
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        // when
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> transactionService.deposit(Deposit.Request.builder()
                        .accountNumber("12341234").amount(1000L).build())
        );
        // then
        assertEquals(404, ex.getHttpStatus());
        assertEquals("계좌를 보유하고 있지 않습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 계좌번호 불일치")
    void test_deposit_failed_when_misMatchAccountNumber() {
        // given
        LocalDateTime now = SeoulDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
        MockFactory.mock_account(user, 0L, now, now, null);
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        // when
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> transactionService.deposit(Deposit.Request.builder()
                        .accountNumber("wrong_account_number").amount(1000L).build())
        );
        // then
        assertEquals(403, ex.getHttpStatus());
        assertEquals("계좌번호가 일치하지 않습니다", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 거래번호 생성 실패")
    void test_deposit_failed_when_errorByGenerateTxnNumber() {
        // given
        LocalDateTime now = SeoulDateTime.now();
        User user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null));
        Account account = MockFactory.mock_account(user, 0L, now, now, null);
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));
        given(transactionRepository.existsByNumber(anyString()))
                .willReturn(true);
        // when
        InternalServerException ex = assertThrows(
                InternalServerException.class,
                () -> transactionService.deposit(Deposit.Request.builder()
                        .accountNumber(account.getNumber()).amount(1000L).build())
        );
        // then
        assertEquals(500, ex.getHttpStatus());
        assertEquals("거래번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요", ex.getErrorMessage());
    }

}