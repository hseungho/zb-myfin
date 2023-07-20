package com.myfin.core.dto;

import com.myfin.core.entity.Account;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AccountDto {

    private Long id;
    private String number;
    private Long balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private UserDto owner;

    public static AccountDto fromEntity(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .balance(entity.getBalance())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .owner(UserDto.fromEntity(entity.getOwner()))
                .build();
    }

}
