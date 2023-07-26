package com.myfin.api.service.impl;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.dto.DeleteAccount;
import com.myfin.api.service.AccountService;
import com.myfin.api.service.TopServiceComponent;
import com.myfin.core.dto.AccountDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.ForbiddenException;
import com.myfin.core.exception.impl.InternalServerException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.Generator;
import com.myfin.security.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends TopServiceComponent implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final PasswordEncoderService passwordEncoderService;

    @Override
    @Transactional
    public AccountDto createAccount(CreateAccount.Request request) {
        validateCreateAccountRequest(request);

        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.create(
                                generateAccountNumber(),
                                passwordEncoderService.encode(request.getAccountPassword()),
                                request.getInitialBalance()
                        ).associate(loginUser())
                )
        );
    }

    @Override
    @Transactional
    public AccountDto deleteAccount(DeleteAccount.Request request) {
        User user = userRepository.findById(loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다"));

        Account account = user.getAccount();

        validateDeleteAccountRequest(request, account);

        account.delete();

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccountRequest(DeleteAccount.Request request, Account account) {
        if (hasNotTexts(request.getAccountNumber(), request.getAccountPassword())) {
            // 계좌 삭제를 위한 계좌번호 또는 계좌비밀번호를 입력하지 않은 경우
            throw new BadRequestException("계좌번호와 계좌비밀번호 모두 입력해주세요");
        }
        if (isMismatch(request.getAccountNumber(), account.getNumber())) {
            // 계좌 삭제 시 요청 계좌번호와 유저의 계좌번호가 불일치한 경우
            throw new ForbiddenException("해당 계좌번호는 유저님의 계좌번호가 아닙니다");
        }
        if (account.isDeleted()) {
            // 이미 계좌가 삭제된 경우
            throw new NotFoundException("이미 삭제된 계좌입니다");
        }
        if (passwordEncoderService.mismatch(request.getAccountPassword(), account.getPassword())) {
            // 계좌 삭제 시 요청 계좌비밀번호와 유저의 계좌비밀번호가 불일치한 경우
            throw new ForbiddenException("계좌비밀번호가 일치하지 않습니다");
        }
        if (account.getBalance() > 0) {
            // 계좌 삭제 시 계좌의 잔액이 0원 이상인 경우
            throw new BadRequestException("계좌에 잔액이 존재한 경우에는 계좌를 삭제할 수 없습니다");
        }
    }

    private String generateAccountNumber() {
        AtomicInteger count = new AtomicInteger();
        while (count.get() < 100) {
            String newAccountNumber = Generator.generateAccountNumber();
            if (!accountRepository.existsByNumber(newAccountNumber)) {
                return newAccountNumber;
            }
            count.getAndIncrement();
        }
        throw new InternalServerException("계좌번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요");
    }

    private void validateCreateAccountRequest(CreateAccount.Request request) {
        if (hasNotTexts(request.getAccountPassword())) {
            // 계좌 생성을 위한 계좌 비밀번호를 입력하지 않은 경우
            throw new BadRequestException("계좌 비밀번호를 입력해주세요");
        }
        if (isInvalidAccountPassword(request.getAccountPassword())) {
            // 계좌 생성을 위한 계좌 비밀번호의 형식이 올바르지 않은 경우
            throw new BadRequestException("계좌 비밀번호는 연속으로 중복되지 않는 4자리 숫자여야 합니다");
        }
        if (hasAccount(loginUser())) {
            // 계좌 생성 시 해당 유저가 이미 계좌를 보유하고 있는 경우
            throw new BadRequestException("이미 계좌를 보유하고 있습니다");
        }
    }

    private boolean hasAccount(User user) {
        return accountRepository.existsByOwner(user);
    }

}
