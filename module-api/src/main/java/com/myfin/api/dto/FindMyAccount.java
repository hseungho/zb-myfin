package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import com.myfin.core.util.DateUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class FindMyAccount {
    @Data
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private AccountResponse account;
        public static Response fromDto(AccountDto dto) {
            return Response.builder()
                    .account(AccountResponse.fromDto(dto))
                    .build();
        }

        @Data
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
        }
    }
}
