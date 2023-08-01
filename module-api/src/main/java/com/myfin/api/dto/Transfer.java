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

        public @NotNull(message = "계좌번호를 입력해주세요") @NotBlank(message = "계좌번호를 입력해주세요") String getAccountNumber() {
            return this.accountNumber;
        }

        public @NotNull(message = "계좌비밀번호를 입력해주세요") @NotBlank(message = "계좌비밀번호를 입력해주세요") String getAccountPassword() {
            return this.accountPassword;
        }

        public @NotNull(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요") @NotBlank(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요") String getReceiver() {
            return this.receiver;
        }

        public @NotNull(message = "송금액을 입력해주세요") @Min(value = 1L, message = "송금액을 1원 이상 입력해주세요") Long getAmount() {
            return this.amount;
        }

        public void setAccountNumber(@NotNull(message = "계좌번호를 입력해주세요") @NotBlank(message = "계좌번호를 입력해주세요") String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setAccountPassword(@NotNull(message = "계좌비밀번호를 입력해주세요") @NotBlank(message = "계좌비밀번호를 입력해주세요") String accountPassword) {
            this.accountPassword = accountPassword;
        }

        public void setReceiver(@NotNull(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요") @NotBlank(message = "수취자 계좌번호 또는 휴대폰번호를 입력해주세요") String receiver) {
            this.receiver = receiver;
        }

        public void setAmount(@NotNull(message = "송금액을 입력해주세요") @Min(value = 1L, message = "송금액을 1원 이상 입력해주세요") Long amount) {
            this.amount = amount;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof Request)) return false;
            final Request other = (Request) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$accountNumber = this.getAccountNumber();
            final Object other$accountNumber = other.getAccountNumber();
            if (this$accountNumber == null ? other$accountNumber != null : !this$accountNumber.equals(other$accountNumber))
                return false;
            final Object this$accountPassword = this.getAccountPassword();
            final Object other$accountPassword = other.getAccountPassword();
            if (this$accountPassword == null ? other$accountPassword != null : !this$accountPassword.equals(other$accountPassword))
                return false;
            final Object this$receiver = this.getReceiver();
            final Object other$receiver = other.getReceiver();
            if (this$receiver == null ? other$receiver != null : !this$receiver.equals(other$receiver))
                return false;
            final Object this$amount = this.getAmount();
            final Object other$amount = other.getAmount();
            if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount))
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
            final Object $accountPassword = this.getAccountPassword();
            result = result * PRIME + ($accountPassword == null ? 43 : $accountPassword.hashCode());
            final Object $receiver = this.getReceiver();
            result = result * PRIME + ($receiver == null ? 43 : $receiver.hashCode());
            final Object $amount = this.getAmount();
            result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
            return result;
        }

        public String toString() {
            return "Transfer.Request(accountNumber=" + this.getAccountNumber() + ", accountPassword=" + this.getAccountPassword() + ", receiver=" + this.getReceiver() + ", amount=" + this.getAmount() + ")";
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
            return "Transfer.Response(account=" + this.getAccount() + ", transaction=" + this.getTransaction() + ")";
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

            public void setNumber(String number) {
                this.number = number;
            }

            public void setBalance(long balance) {
                this.balance = balance;
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
                return result;
            }

            public String toString() {
                return "Transfer.Response.AccountResponse(number=" + this.getNumber() + ", balance=" + this.getBalance() + ")";
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

            public void setNumber(String number) {
                this.number = number;
            }

            public void setAmount(long amount) {
                this.amount = amount;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setReceiverName(String receiverName) {
                this.receiverName = receiverName;
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
                final Object this$receiverName = this.getReceiverName();
                final Object other$receiverName = other.getReceiverName();
                if (this$receiverName == null ? other$receiverName != null : !this$receiverName.equals(other$receiverName))
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
                final Object $receiverName = this.getReceiverName();
                result = result * PRIME + ($receiverName == null ? 43 : $receiverName.hashCode());
                final Object $tradedAt = this.getTradedAt();
                result = result * PRIME + ($tradedAt == null ? 43 : $tradedAt.hashCode());
                return result;
            }

            public String toString() {
                return "Transfer.Response.TransactionResponse(number=" + this.getNumber() + ", amount=" + this.getAmount() + ", type=" + this.getType() + ", receiverName=" + this.getReceiverName() + ", tradedAt=" + this.getTradedAt() + ")";
            }
        }
    }
}
