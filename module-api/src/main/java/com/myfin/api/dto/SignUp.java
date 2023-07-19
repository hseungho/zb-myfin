package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class SignUp {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
        @NotBlank
        private String userName;
        @NotNull
        private LocalDate birthDate;
        @NotBlank
        private String address1;
        private String address2;
        @NotBlank
        private String phoneNum;
        private String email;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private String userId;
        private String userName;
        private String createdAt;
    }
}
