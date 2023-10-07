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

import java.util.Objects;

public class Deposit {
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "계좌번호를 입력해주세요")
        @NotBlank(message = "계좌번호를 입력해주세요")
        private String accountNumber;
        @NotNull(message = "입금액을 입력해주세요")
        @Min(value = 1L, message = "입금액을 1원 이상 입력해주세요")
        private Long amount;

        public @NotNull(message = "계좌번호를 입력해주세요") @NotBlank(message = "계좌번호를 입력해주세요") String getAccountNumber() {
            return this.accountNumber;
        }

        public @NotNull(message = "입금액을 입력해주세요") @Min(value = 1L, message = "입금액을 1원 이상 입력해주세요") Long getAmount() {
            return this.amount;
        }

        public void setAccountNumber(@NotNull(message = "계좌번호를 입력해주세요") @NotBlank(message = "계좌번호를 입력해주세요") String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setAmount(@NotNull(message = "입금액을 입력해주세요") @Min(value = 1L, message = "입금액을 1원 이상 입력해주세요") Long amount) {
            this.amount = amount;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof Request)) return false;
            final Request other = (Request) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$accountNumber = this.getAccountNumber();
            final Object other$accountNumber = other.getAccountNumber();
            if (!Objects.equals(this$accountNumber, other$accountNumber))
                return false;
            final Object this$amount = this.getAmount();
            final Object other$amount = other.getAmount();
            if (!Objects.equals(this$amount, other$amount))
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof Request;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $accountNumber = this.getAccountNumber();
            result = result * PRIME + ($accountNumber == null ? 43 : $accountNumber.hashCode());
            final Object $amount = this.getAmount();
            result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
            return result;
        }

        public String toString() {
            return "Deposit.Request(accountNumber=" + this.getAccountNumber() + ", amount=" + this.getAmount() + ")";
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

        public void setAccount(AccountResponse account) {
            this.account = account;
        }

        public void setTransaction(TransactionResponse transaction) {
            this.transaction = transaction;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof Response)) return false;
            final Response other = (Response) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$account = this.getAccount();
            final Object other$account = other.getAccount();
            if (this$account == null ? other$account != null : !this$account.equals(other$account))
                return false;
            final Object this$transaction = this.getTransaction();
            final Object other$transaction = other.getTransaction();
            if (this$transaction == null ? other$transaction != null : !this$transaction.equals(other$transaction))
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof Response;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $account = this.getAccount();
            result = result * PRIME + ($account == null ? 43 : $account.hashCode());
            final Object $transaction = this.getTransaction();
            result = result * PRIME + ($transaction == null ? 43 : $transaction.hashCode());
            return result;
        }

        public String toString() {
            return "Deposit.Response(account=" + this.getAccount() + ", transaction=" + this.getTransaction() + ")";
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        private static class AccountResponse {
            private String number;
            private long balance;
            private String createdAt;
            private String updatedAt;

            private static AccountResponse fromDto(AccountDto dto) {
                return AccountResponse.builder()
                        .number(dto.getNumber())
                        .balance(dto.getBalance())
                        .createdAt(DateUtil.getDateTimeIfPresent(dto.getCreatedAt()))
                        .updatedAt(DateUtil.getDateTimeIfPresent(dto.getUpdatedAt()))
                        .build();
            }

            public String getNumber() {
                return this.number;
            }

            public long getBalance() {
                return this.balance;
            }

            public String getCreatedAt() {
                return this.createdAt;
            }

            public String getUpdatedAt() {
                return this.updatedAt;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public void setBalance(long balance) {
                this.balance = balance;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof AccountResponse))
                    return false;
                final AccountResponse other = (AccountResponse) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$number = this.getNumber();
                final Object other$number = other.getNumber();
                if (this$number == null ? other$number != null : !this$number.equals(other$number))
                    return false;
                if (this.getBalance() != other.getBalance()) return false;
                final Object this$createdAt = this.getCreatedAt();
                final Object other$createdAt = other.getCreatedAt();
                if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt))
                    return false;
                final Object this$updatedAt = this.getUpdatedAt();
                final Object other$updatedAt = other.getUpdatedAt();
                if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt))
                    return false;
                return true;
            }

            protected boolean canEqual(final Object other) {
                return other instanceof AccountResponse;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $number = this.getNumber();
                result = result * PRIME + ($number == null ? 43 : $number.hashCode());
                final long $balance = this.getBalance();
                result = result * PRIME + (int) ($balance >>> 32 ^ $balance);
                final Object $createdAt = this.getCreatedAt();
                result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
                final Object $updatedAt = this.getUpdatedAt();
                result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
                return result;
            }

            public String toString() {
                return "Deposit.Response.AccountResponse(number=" + this.getNumber() + ", balance=" + this.getBalance() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
            }
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
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

            public String getTradedAt() {
                return this.tradedAt;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public void setAmount(long amount) {
                this.amount = amount;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setTradedAt(String tradedAt) {
                this.tradedAt = tradedAt;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof TransactionResponse))
                    return false;
                final TransactionResponse other = (TransactionResponse) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$number = this.getNumber();
                final Object other$number = other.getNumber();
                if (this$number == null ? other$number != null : !this$number.equals(other$number))
                    return false;
                if (this.getAmount() != other.getAmount()) return false;
                final Object this$type = this.getType();
                final Object other$type = other.getType();
                if (this$type == null ? other$type != null : !this$type.equals(other$type))
                    return false;
                final Object this$tradedAt = this.getTradedAt();
                final Object other$tradedAt = other.getTradedAt();
                if (this$tradedAt == null ? other$tradedAt != null : !this$tradedAt.equals(other$tradedAt))
                    return false;
                return true;
            }

            protected boolean canEqual(final Object other) {
                return other instanceof TransactionResponse;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $number = this.getNumber();
                result = result * PRIME + ($number == null ? 43 : $number.hashCode());
                final long $amount = this.getAmount();
                result = result * PRIME + (int) ($amount >>> 32 ^ $amount);
                final Object $type = this.getType();
                result = result * PRIME + ($type == null ? 43 : $type.hashCode());
                final Object $tradedAt = this.getTradedAt();
                result = result * PRIME + ($tradedAt == null ? 43 : $tradedAt.hashCode());
                return result;
            }

            public String toString() {
                return "Deposit.Response.TransactionResponse(number=" + this.getNumber() + ", amount=" + this.getAmount() + ", type=" + this.getType() + ", tradedAt=" + this.getTradedAt() + ")";
            }
        }
    }
}
