package com.myfin.core.entity;

import com.myfin.core.BaseEntity;
import com.myfin.core.config.EncryptConverter;
import com.myfin.core.type.SexType;
import com.myfin.core.type.UserType;
import com.myfin.core.util.SeoulDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    /** User PK ID */
    @Id
    @GenericGenerator(name = "uuidGen", strategy = "com.myfin.core.util.UUIDGenerator")
    @GeneratedValue(generator = "uuidGen")
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    /** 유저 아이디 */
    @Column(name = "user_id", nullable = false, unique = true)
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
    @Column(name = "sex_flag", nullable = false, columnDefinition = "tinyint not null")
    private SexType sex;

    /** 유저의 주소 정보 */
    @Embedded
    private UserAddressVO userAddress;

    /** 유저의 휴대폰번호 (encrypted) */
    @Column(name = "phone_num", nullable = false, unique = true)
    @Convert(converter = EncryptConverter.class)
    private String phoneNum;

    /** 유저의 이메일 주소 */
    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserType type;

    /** 유저의 마지막 로그인 일시 */
    @Column(name = "last_logged_in_at")
    private LocalDateTime lastLoggedInAt;

    /** 유저의 삭제일시 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Transient
    private Boolean isOnLoginRequest = false;

    public static User create(String userId,
                              String password,
                              String name,
                              LocalDate birthDate,
                              boolean sex,
                              String zipCode,
                              String address1,
                              String address2,
                              String phoneNum,
                              String email
    ) {
        return User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .birthDate(birthDate)
                .sex(SexType.of(sex))
                .userAddress(UserAddressVO.of(zipCode, address1, address2))
                .phoneNum(phoneNum)
                .email(email)
                .type(UserType.ROLE_USER)
                .build();
    }

    @Override
    public void preUpdate() {
        if (isOnLoginRequest) {
            isOnLoginRequest = false;
            return;
        }
        super.preUpdate();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(type.name()));
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isResigned() {
        return deletedAt != null || type == UserType.RESIGNED;
    }

    public void login() {
        this.lastLoggedInAt = SeoulDateTime.now();
        isOnLoginRequest = true;
    }
}
