package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class VerifyRequestIdentity {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        private String phoneNum;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String requestedAt;

        public static Response of(LocalDateTime requestedAt) {
            return Response.builder()
                    .requestedAt(requestedAt.toString())
                    .build();
        }
    }

}
