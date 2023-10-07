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

            final String userId = "tester";
            final String userPw = "password1234!";
            final String userName = "테스터";
            final LocalDate birthDate = LocalDate.of(1997, 1, 1);
            final boolean userSex = false;
            final String zipCode = "10001";
            final String address1 = "서울특별시 강남구 도산대로 17길";
            final String address2 = "10001호";
            final String phoneNum = "01012341234";
            final String email = "tester@gmail.com";

            User user = User.create(
                    userId,
                    passwordEncoderService.encode(userPw),
                    userName,
                    birthDate,
                    userSex,
                    zipCode,
                    address1,
                    address2,
                    phoneNum,
                    email
            );
            Field id = user.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(user, "tester_id");
            userRepository.save(user);

            final String accNumber = "1234123412341234";
            final String accPw = "1234";
            final Long balance = 100000L;
            Account account = Account.create(
                    accNumber,
                    passwordEncoderService.encode(accPw),
                    balance
            ).associate(user);
            accountRepository.save(account);

            final String userId2 = "tester2";
            final String userPw2 = "password1234!";
            final String userName2 = "테스터2";
            final LocalDate birthDate2 = LocalDate.of(1997, 1, 1);
            final boolean userSex2 = false;
            final String zipCode2 = "10002";
            final String address1_2 = "서울특별시 강남구 도산대로 17길";
            final String address2_2 = "10002호";
            final String phoneNum2 = "01043214321";
            final String email2 = "tester2@gmail.com";

            User user2 = User.create(
                    userId2,
                    passwordEncoderService.encode(userPw2),
                    userName2,
                    birthDate2,
                    userSex2,
                    zipCode2,
                    address1_2,
                    address2_2,
                    phoneNum2,
                    email2
            );
            Field id2 = user2.getClass().getDeclaredField("id");
            id2.setAccessible(true);
            id2.set(user2, "tester_id_2");
            userRepository.save(user2);

            final String accNumber2 = "4321432143214321";
            final String accPw2 = "1234";
            final Long balance2 = 100000L;
            Account account2 = Account.create(
                    accNumber2,
                    passwordEncoderService.encode(accPw2),
                    balance2
            ).associate(user2);
            accountRepository.save(account2);

        } catch (Exception e) {
            log.error("Occurred Exception during DB init.", e);
        }
    }


}
