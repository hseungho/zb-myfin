package com.myfin.core.entity;

import com.myfin.core.util.EncryptConverter;
import com.myfin.core.type.TransactionType;
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

    @Column(name = "rct_act_no")
    @Convert(converter = EncryptConverter.class)
    private String recipientAccountNumber;

    @Column(name = "traded_at", nullable = false)
    private LocalDateTime tradedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

}
