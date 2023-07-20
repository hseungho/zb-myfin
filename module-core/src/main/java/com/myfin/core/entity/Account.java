package com.myfin.core.entity;

import com.myfin.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity(name = "account")
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
    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

}
