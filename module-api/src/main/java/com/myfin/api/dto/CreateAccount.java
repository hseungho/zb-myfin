package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import com.myfin.core.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CreateAccount {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "계좌비밀번호를 입력해주세요")
        @NotBlank(message = "계좌비밀번호를 입력해주세요")
        private String accountPassword;
        private Long initialBalance = 0L;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response extends ATopResponse {
        private UserResponse user;
        private AccountResponse account;

        public static Response fromDto(AccountDto dto) {
            return Response.builder()
                    .user(UserResponse.fromDto(dto.getOwner()))
                    .account(AccountResponse.fromDto(dto))
                    .build();
        }

        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class UserResponse {
            private String name;
            private String phoneNum;
            private String email;
            private String createdAt;

            private static UserResponse fromDto(UserDto dto) {
                return UserResponse.builder()
                        .name(dto.getName())
                        .phoneNum(dto.getPhoneNum())
                        .email(dto.getEmail())
                        .createdAt(getDateTimeIfPresent(dto.getCreatedAt()))
                        .build();
            }
        }

        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class AccountResponse {
            private String number;
            private long balance;
            private String createdAt;

            private static AccountResponse fromDto(AccountDto dto) {
                return AccountResponse.builder()
                        .number(dto.getNumber())
                        .balance(dto.getBalance())
                        .createdAt(getDateTimeIfPresent(dto.getCreatedAt()))
                        .build();
            }
        }
    }
}
