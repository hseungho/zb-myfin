package com.myfin.core.entity;

import com.myfin.core.util.EncryptConverter;
import com.myfin.core.type.TransactionType;
import com.myfin.core.util.SeoulDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity(name = "transaction")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_no", nullable = false, unique = true)
    @Convert(converter = EncryptConverter.class)
    private String number;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "txn_type", nullable = false)
    private TransactionType type;

    @Column(name = "traded_at", nullable = false)
    private LocalDateTime tradedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_act_id")
    private Account sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_act_id")
    private Account receiver;

    public static Transaction createDeposit(String number,
                                            Long amount,
                                            Account sender) {
        return Transaction.builder()
                .number(number)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .tradedAt(SeoulDateTime.now())
                .sender(sender)
                .receiver(sender)
                .build();
    }

    public static Transaction createWithdrawal(String number,
                                               Long amount,
                                               Account sender) {
        return Transaction.builder()
                .number(number)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .tradedAt(SeoulDateTime.now())
                .sender(sender)
                .receiver(sender)
                .build();
    }
}
