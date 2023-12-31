package com.myfin.api.dto;

import com.myfin.core.util.DateUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class Login {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "아이디를 입력해주세요")
        private String userId;
        @NotBlank(message = "패스워드를 입력해주세요")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String accessToken;
        private String refreshToken;
        private String lastLoggedInAt;

        public static Response fromDto(TokenDto dto) {
            return Response.builder()
                    .accessToken(dto.getAccessToken())
                    .refreshToken(dto.getRefreshToken())
                    .lastLoggedInAt(DateUtil.getDateTimeIfPresent(dto.getLastLoggedInAt()))
                    .build();
        }
    }
}
