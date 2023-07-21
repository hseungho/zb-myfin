package com.myfin.api.config;

import com.myfin.core.entity.User;
import com.myfin.core.repository.UserRepository;
import com.myfin.security.service.EncryptService;
import com.myfin.security.service.PasswordEncoderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class H2DBInit {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final EncryptService encryptService;

    @PostConstruct
    @Transactional
    public void init() {
        final String user_id = "tester";
        final String user_pw = "password1234!";
        final String user_name = "테스터";
        final LocalDate birth_date = LocalDate.of(1997, 1, 1);
        final boolean user_sex = false;
        final String zip_code = "10001";
        final String address_1 = "서울특별시 강남구 도산대로 17길";
        final String address_2 = "10001호";
        final String phone_num = "01012341234";
        final String email = "tester@gmail.com";

        try {
            User user = User.create(
                    user_id,
                    passwordEncoderService.encode(user_pw),
                    user_name,
                    birth_date,
                    user_sex,
                    zip_code,
                    address_1,
                    address_2,
                    phone_num,
                    email
            );
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Occurred Exception during DB init.", e);
        }
    }


}
