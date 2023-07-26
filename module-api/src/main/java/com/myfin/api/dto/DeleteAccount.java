package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DeleteAccount {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "계좌번호를 입력해주세요")
        @NotBlank(message = "계좌번호를 입력해주세요")
        private String accountNumber;
        @NotNull(message = "계좌비밀번호를 입력해주세요")
        @NotBlank(message = "계좌비밀번호를 입력해주세요")
        private String accountPassword;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response extends ATopResponse {
        private String accountNumber;
        private String createdAt;
        private String deletedAt;

        public static Response fromDto(AccountDto dto) {
            return Response.builder()
                    .accountNumber(dto.getNumber())
                    .createdAt(getDateTimeIfPresent(dto.getCreatedAt()))
                    .deletedAt(getDateTimeIfPresent(dto.getDeletedAt()))
                    .build();
        }
    }
}
