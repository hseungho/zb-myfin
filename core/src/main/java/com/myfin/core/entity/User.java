package com.myfin.core.entity;

import com.myfin.core.type.SexType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {

    /** User PK ID */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    /** 유저 아이디 */
    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private String userId;

    /** 유저 패스워드 */
    @Column(name = "user_pw", nullable = false)
    private String password;

    /** 유저의 성명 */
    @Column(name = "user_name", nullable = false)
    private String name;

    /** 유저의 생년월일 */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /** 유저의 성별. (남: MALE(0), 여: FEMALE(1)) */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "sex_flag", nullable = false, columnDefinition = "TINYINT(1) NOT NULL")
    private SexType sex;

    /** 유저의 우편번호 */
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    /** 유저의 도로명 주소 */
    @Column(name = "address_1", nullable = false)
    private String address1;

    /** 유저의 상세 주소 */
    @Column(name = "address_2")
    private String address2;

    /** 유저의 휴대폰번호 */
    @Column(name = "phone_num", nullable = false, unique = true)
    private String phoneNum;

    /** 유저의 이메일 주소 */
    @Column(name = "email")
    private String email;

    /** 유저의 마지막 로그인 일시 */
    @Column(name = "last_logged_in_at")
    private LocalDateTime lastLoggedInAt;

    /** 유저의 삭제일시 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
