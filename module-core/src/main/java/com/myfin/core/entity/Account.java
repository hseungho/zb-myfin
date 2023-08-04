package com.myfin.core.entity;

import com.myfin.core.BaseEntity;
import com.myfin.core.util.EncryptConverter;
import com.myfin.core.util.SeoulDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity(name = "account")
@Table(
        name = "account",
        indexes = @Index(name = "idx__act_no", columnList = "act_no")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

    /** Account PK ID */
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 계좌번호 */
    @Column(name = "act_no", nullable = false, unique = true)
    @Convert(converter = EncryptConverter.class)
    private String number;

    /** 계좌 비밀번호 */
    @Column(name = "act_pw", nullable = false)
    private String password;

    /** 계좌 잔액 */
    @Column(name = "balance")
    private Long balance;

    /** 계좌 삭제일시 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 계좌 소유자 */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User owner;

    public static Account create(String number, String encryptedPassword, Long balance) {
        return Account.builder()
                .number(number)
                .password(encryptedPassword)
                .balance(balance)
                .build();
    }

    public Account associate(User owner) {
        this.owner = owner;
        owner.associate(this);
        return this;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void delete(final String deletedPassword) {
        this.number = "DEL_" + this.number;
        this.password = deletedPassword;
        this.deletedAt = SeoulDateTime.now();
        this.owner = null;
    }

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void withdrawal(Long amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }

    public boolean cannotWithdrawal(Long amount) {
        synchronized (this) {
            return this.balance < amount;
        }
    }
}

