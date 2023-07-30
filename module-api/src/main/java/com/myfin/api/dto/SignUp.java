package com.myfin.api.dto;

import com.myfin.core.dto.UserDto;
import com.myfin.core.util.DateUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SignUp {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "아이디를 입력해주세요")
        @NotBlank(message = "아이디를 입력해주세요")
        private String userId;
        @NotBlank(message = "패스워드를 입력해주세요")
        @Length(min = 8, message = "패스워드는 8자리 이상으로 입력해주세요")
        private String password;
        @NotBlank(message = "성명을 입력해주세요")
        private String userName;
        @NotNull(message = "생년월일을 입력해주세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;
        /** 성별. 남자일 경우 false, 여자일 경우 true */
        @NotNull(message = "성별을 선택해주세요")
        private Boolean sex;
        @NotBlank(message = "우편번호를 입력해주세요")
        private String zipCode;
        @NotBlank(message = "도로명주소를 입력해주세요")
        private String address1;
        private String address2;
        @NotBlank(message = "휴대폰번호를 입력해주세요")
        private String phoneNum;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String userId;
        private String userName;
        private String createdAt;

        public static Response fromDto(UserDto dto) {
            return Response.builder()
                    .userId(dto.getUserId())
                    .userName(dto.getName())
                    .createdAt(DateUtil.getDateTimeIfPresent(dto.getCreatedAt()))
                    .build();
        }
    }
}
