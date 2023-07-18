package com.myfin.core.entity;

import com.myfin.core.type.SexType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String id;

    private String userId;

    private String password;

    private String name;

    private LocalDate birthDate;

    private SexType sex;

    private String zipCode;

    private String address1;

    private String address2;

    private String phoneNum;

    private String email;

    private LocalDateTime lastLoggedInAt;

    private LocalDateTime deletedAt;
}
