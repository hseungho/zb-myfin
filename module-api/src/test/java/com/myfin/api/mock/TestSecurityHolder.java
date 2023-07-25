package com.myfin.api.mock;

import com.myfin.core.entity.User;
import com.myfin.core.entity.UserAddressVO;
import com.myfin.core.type.SexType;
import com.myfin.core.type.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

public class TestSecurityHolder {
    public static User setSecurityHolderUser(UserType type) {
        User user = User.builder()
                .id("user_id")
                .name("user_name")
                .birthDate(LocalDate.of(1997, 1, 1))
                .sex(SexType.MALE)
                .userAddress(UserAddressVO.of("zipcode", "address_1", "address_2"))
                .phoneNum("01012341234")
                .email("user@test.com")
                .type(type)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );
        return user;
    }
}
