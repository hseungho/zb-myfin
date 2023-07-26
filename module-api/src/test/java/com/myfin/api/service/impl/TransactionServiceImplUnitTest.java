package com.myfin.api.service.impl;

import com.myfin.api.dto.Deposit;
import com.myfin.api.mock.MockFactory;
import com.myfin.api.mock.TestSecurityHolder;
import com.myfin.core.dto.TransactionDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.Transaction;
import com.myfin.core.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

}