package com.myfin.api.service.impl;

import com.myfin.api.dto.Deposit;
import com.myfin.api.mock.MockFactory;
import com.myfin.api.service.TransactionService;
import com.myfin.api.service.UserAuthenticationService;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.type.UserType;
import com.myfin.security.service.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TransactionServiceImplLockTest {

    @Autowired private TransactionService transactionService;

    @Autowired private PasswordEncoderService passwordEncoderService;

    @Autowired private UserAuthenticationService userAuthenticationService;

    @Autowired private UserRepository userRepository;

    @Autowired private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    public void init() {
        User user = MockFactory.mock_user_for_db(UserType.ROLE_USER, passwordEncoderService.encode("test1234!"));
        User save_user = userRepository.save(user);
        account = MockFactory.mock_account_for_db(save_user, 0L);
        accountRepository.save(account);

        SecurityContextHolder.getContext().setAuthentication(
                userAuthenticationService.getAuthentication(user.getId())
        );
    }

    /**
     * 계좌 입금 동시성 및 Redisson 테스트 코드. <br>
     * 하지만, 멀티모듈의 특성 때문인지, 또다른 설정이 필요한건지 @SpringBootTest로 진행하여도, <br>
     * TransactionService의 메소드를 실행하지 못하고 있다. <br>
     * 예상하기로는 멀티모듈의 특성 상 ComponentScan을 지정하고 있는데, SpringBootTest로 <br>
     * 테스트 진행 시 해당 bean을 찾지 못하는 것이 아닌가 싶다. <br>
     * -> 멘토님의 피드백이 필요!!
     */
//    @Test
//    @DisplayName("계좌 입금 동시성 테스트")
    void test_deposit_concurrency_using_redisson() throws Exception {
        // given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        long amount = 1000L;

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    transactionService.deposit(Deposit.Request.builder()
                            .accountNumber("account_number").amount(amount).build());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Account persistAccount = accountRepository.findById(account.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(numberOfThreads*amount, account.getBalance());
        System.out.println("계좌 잔고 = " + persistAccount.getBalance());
    }

}
