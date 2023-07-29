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
    private LocalDateTime tradedAt;
    private AccountDto sender;
    private AccountDto receiver;

    public static TransactionDto fromEntity(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .amount(entity.getAmount())
                .type(entity.getType())
                .tradedAt(entity.getTradedAt())
                .sender(AccountDto.fromEntity(entity.getSender()))
                .receiver(AccountDto.fromEntity(entity.getReceiver()))
                .build();
    }

}
