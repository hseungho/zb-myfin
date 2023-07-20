package com.myfin.core.dto;

import com.myfin.core.entity.Transaction;
import com.myfin.core.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class TransactionDto {

    private Long id;
    private String number;
    private Long amount;
    private TransactionType type;
    private String recipientAccountNumber;
    private LocalDateTime tradedAt;
    private AccountDto account;

    public static TransactionDto fromEntity(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .amount(entity.getAmount())
                .type(entity.getType())
                .recipientAccountNumber(entity.getRecipientAccountNumber())
                .tradedAt(entity.getTradedAt())
                .account(AccountDto.fromEntity(entity.getAccount()))
                .build();
    }

}
