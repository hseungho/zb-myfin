package com.myfin.api.dto;

import com.myfin.core.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;
        /** 성별. 남자일 경우 false, 여자일 경우 true */
        @NotNull
        private Boolean sex;
        @NotBlank
        private String zipCode;
        @NotBlank
        private String address1;
        private String address2;
        @NotBlank
        private String phoneNum;
        private String email;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response extends ATopResponse {
        private String userId;
        private String userName;
        private String createdAt;

        public static Response fromDto(UserDto dto) {
            return Response.builder()
                    .userId(dto.getUserId())
                    .userName(dto.getName())
                    .createdAt(getDateTimeIfPresent(dto.getCreatedAt()))
                    .build();
        }
    }
}
