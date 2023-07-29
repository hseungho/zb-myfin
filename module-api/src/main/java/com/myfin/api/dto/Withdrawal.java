package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import com.myfin.core.dto.TransactionDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class Withdrawal {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "계좌번호를 입력해주세요")
        @NotBlank(message = "계좌번호를 입력해주세요")
        private String accountNumber;
        @NotNull(message = "계좌비밀번호를 입력해주세요")
        @NotBlank(message = "계좌비밀번호를 입력해주세요")
        private String accountPassword;
        @NotNull(message = "출금액을 입력해주세요")
        @Min(value = 1L, message = "출금액을 1원 이상 입력해주세요")
        private Long amount;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response extends TopResponse {
        private AccountResponse account;
        private TransactionResponse transaction;

        public static Response fromDto(TransactionDto dto) {
            return Response.builder()
                    .account(AccountResponse.fromDto(dto.getSender()))
                    .transaction(TransactionResponse.fromDto(dto))
                    .build();
        }
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class AccountResponse {
            private String number;
            private long balance;
            private String createdAt;
            private String updatedAt;
            private static AccountResponse fromDto(AccountDto dto) {
                return AccountResponse.builder()
                        .number(dto.getNumber())
                        .balance(dto.getBalance())
                        .createdAt(getDateTimeIfPresent(dto.getCreatedAt()))
                        .updatedAt(getDateTimeIfPresent(dto.getUpdatedAt()))
                        .build();
            }
        }

        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class TransactionResponse {
            private String number;
            private long amount;
            private String type;
            private String tradedAt;
            private static TransactionResponse fromDto(TransactionDto dto) {
                return TransactionResponse.builder()
                        .number(dto.getNumber())
                        .amount(dto.getAmount())
                        .type(dto.getType().name())
                        .tradedAt(getDateTimeIfPresent(dto.getTradedAt()))
                        .build();
            }
        }
    }
}
