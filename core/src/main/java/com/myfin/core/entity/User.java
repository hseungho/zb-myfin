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

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "user_pw", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "sex_flag", nullable = false)
    private SexType sex;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "address_1", nullable = false)
    private String address1;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @Column(name = "email")
    private String email;

    @Column(name = "last_logged_in_at")
    private LocalDateTime lastLoggedInAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
