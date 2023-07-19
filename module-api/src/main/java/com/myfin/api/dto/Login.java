package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class Login {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response extends ATopResponse {
        private String accessToken;
        private String refreshToken;
        private String lastLoggedInAt;

        public static Response fromDto(TokenDto dto) {
            return Response.builder()
                    .accessToken(dto.getAccessToken())
                    .refreshToken(dto.getRefreshToken())
                    .lastLoggedInAt(getDateTimeIfPresent(dto.getLastLoggedInAt()))
                    .build();
        }
    }
}
