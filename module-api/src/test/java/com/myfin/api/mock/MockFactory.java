package com.myfin.api.mock;

import com.myfin.core.entity.Account;
import com.myfin.core.entity.Transaction;
import com.myfin.core.entity.User;
import com.myfin.core.entity.UserAddressVO;
import com.myfin.core.type.SexType;
import com.myfin.core.type.TransactionType;
import com.myfin.core.type.UserType;
import com.myfin.core.util.UUIDGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MockFactory {

    public static User mock_user_for_db(UserType type, String encryptedPassword) {
        return User.builder()
                .id(UUIDGenerator.generate())
                .userId("user_id")
                .password(encryptedPassword)
                .name("user_name")
                .birthDate(LocalDate.of(1997, 1, 1))
                .sex(SexType.MALE)
                .userAddress(UserAddressVO.of("zipcode", "address_1", "address_2"))
                .phoneNum("01012341234")
                .email("user@test.com")
                .type(type)
                .build();
    }

    public static User mock_user(UserType type) {
        return mock_user(type, null, null, null);
    }

    public static User mock_user(UserType type,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt,
                                 LocalDateTime deletedAt) {
        return User.builder()
                .id("user_id")
                .userId("users_user_id")
                .name("user_name")
                .birthDate(LocalDate.of(1997, 1, 1))
                .sex(SexType.MALE)
                .userAddress(UserAddressVO.of("zipcode", "address_1", "address_2"))
                .phoneNum("01012341234")
                .email("user@test.com")
                .type(type)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public static Account mock_account_for_db(User owner, Long balance) {
        return Account.builder()
                .number("account_number")
                .password("1234")
                .balance(balance)
                .build()
                .associate(owner);
    }

    public static Account mock_account(User owner, Long balance) {
        return mock_account(owner, balance, null, null, null);
    }

    public static Account mock_account(User owner,
                                       Long balance,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt,
                                       LocalDateTime deletedAt) {
        return Account.builder()
                .id(1L)
                .number("account_number")
                .password("1234")
                .balance(balance)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build()
                .associate(owner);
    }

    public static Transaction mock_transaction(Account account,
                                               Long amount,
                                               String rctAccountNumber,
                                               TransactionType type,
                                               LocalDateTime tradedAt) {
        return Transaction.builder()
                .id(1L)
                .number("txn_number")
                .amount(amount)
                .recipientAccountNumber(rctAccountNumber)
                .type(type)
                .tradedAt(tradedAt)
                .account(account)
                .build();
    }
}
