package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Login {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private String accessToken;
        private String refreshToken;
        private String lastLoggedInAt;

        public static Response fromDto() {
            return Response.builder()
                    .build();
        }
    }
}
