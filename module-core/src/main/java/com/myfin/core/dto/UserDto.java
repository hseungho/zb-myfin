package com.myfin.core.dto;

import com.myfin.core.entity.User;
import com.myfin.core.type.SexType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UserDto {

    private String id;
    private String userId;
    private String name;
    private LocalDate birthDate;
    private SexType sex;
    private String zipCode;
    private String address1;
    private String address2;
    private String phoneNum;
    private String email;
    private LocalDateTime lastLoggedInAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static UserDto fromEntity(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .birthDate(entity.getBirthDate())
                .sex(entity.getSex())
                .zipCode(entity.getUserAddress().getZipCode())
                .address1(entity.getUserAddress().getAddress1())
                .address2(entity.getUserAddress().getAddress2())
                .phoneNum(entity.getPhoneNum())
                .email(entity.getEmail())
                .lastLoggedInAt(entity.getLastLoggedInAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

}
