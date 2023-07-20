package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class VerifyIdentity {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        private String phoneNum;
        @NotBlank(message = "인증코드를 입력해주세요")
        private String code;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private boolean result;
        private String message;

        public static Response fromDto(VerifyIdentityResultDto dto) {
            return Response.builder()
                    .result(dto.isResult())
                    .message(dto.getMessage())
                    .build();
        }
    }
}
