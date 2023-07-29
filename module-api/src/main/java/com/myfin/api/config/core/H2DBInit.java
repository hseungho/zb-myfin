package com.myfin.api.config.core;

import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.security.service.EncryptService;
import com.myfin.security.service.PasswordEncoderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class H2DBInit {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final EncryptService encryptService;

    @PostConstruct
    @Transactional
    public void init() {
        try {

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
            Field id = user.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(user, "tester_id");
            userRepository.save(user);

            final String acc_number = "1234123412341234";
            final String acc_pw = "1234";
            final Long balance = 100000L;
            Account account = Account.create(
                    acc_number,
                    passwordEncoderService.encode(acc_pw),
                    balance
            ).associate(user);
            accountRepository.save(account);

        } catch (Exception e) {
            log.error("Occurred Exception during DB init.", e);
        }
    }


}
