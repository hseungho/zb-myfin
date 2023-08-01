package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import com.myfin.core.dto.TransactionDto;
import com.myfin.core.util.DateUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class Transfer {
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "계좌번호를 입력해주세요")
        @NotBlank(message = "계좌번호를 입력해주세요")
        private String accountNumber;
        @NotNull(message = "계좌비밀번호를 입력해주세요")
        @NotBlank(message = "계좌비밀번호를 입력해주세요")
        private String accountPassword;
        @NotNull(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요")
        @NotBlank(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요")
        private String receiver;
        @NotNull(message = "송금액을 입력해주세요")
        @Min(value = 1L, message = "송금액을 1원 이상 입력해주세요")
        private Long amount;

        private String receiverAccountNumber;

        public String getReceiverAccountNumber() {
            return this.receiverAccountNumber;
        }

        public void setReceiverAccountNumber(String receiverAccountNumber) {
            this.receiverAccountNumber = receiverAccountNumber;
        }

        public String getAccountNumber() {
            return this.accountNumber;
        }

        public String getAccountPassword() {
            return this.accountPassword;
        }

        public String getReceiver() {
            return this.receiver;
        }

        public Long getAmount() {
            return this.amount;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private AccountResponse account;
        private TransactionResponse transaction;
        public static Response fromDto(TransactionDto dto) {
            return Response.builder()
                    .account(AccountResponse.fromDto(dto.getSender()))
                    .transaction(TransactionResponse.fromDto(dto))
                    .build();
        }

        public AccountResponse getAccount() {
            return this.account;
        }

        public TransactionResponse getTransaction() {
            return this.transaction;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        private static class AccountResponse {
            private String number;
            private long balance;
            private static AccountResponse fromDto(AccountDto dto) {
                return AccountResponse.builder()
                        .number(dto.getNumber())
                        .balance(dto.getBalance())
                        .build();
            }

            public String getNumber() {
                return this.number;
            }

            public long getBalance() {
                return this.balance;
            }
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        private static class TransactionResponse {
            private String number;
            private long amount;
            private String type;
            private String receiverName;
            private String tradedAt;
            private static TransactionResponse fromDto(TransactionDto dto) {
                return TransactionResponse.builder()
                        .number(dto.getNumber())
                        .amount(dto.getAmount())
                        .type(dto.getType().name())
                        .receiverName(dto.getReceiver().getOwner().getName())
                        .tradedAt(DateUtil.getDateTimeIfPresent(dto.getTradedAt()))
                        .build();
            }

            public String getNumber() {
                return this.number;
            }

            public long getAmount() {
                return this.amount;
            }

            public String getType() {
                return this.type;
            }

            public String getReceiverName() {
                return this.receiverName;
            }

            public String getTradedAt() {
                return this.tradedAt;
            }
        }
    }
}
