package com.myfin.api.service.impl;

import com.myfin.api.dto.Deposit;
import com.myfin.api.dto.Withdrawal;
import com.myfin.api.service.TopServiceComponent;
import com.myfin.api.service.TransactionService;
import com.myfin.core.dto.TransactionDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.Transaction;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.ForbiddenException;
import com.myfin.core.exception.impl.InternalServerException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.repository.TransactionRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.Generator;
import com.myfin.redis.lock.AccountLock;
import com.myfin.security.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final PasswordEncoderService passwordEncoderService;
    private final AccountUserSearchService accountUserSearchService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @AccountLock(key = "#request.getAccountNumber()")
    public TransactionDto deposit(Deposit.Request request) {
        User user = userRepository.findById(SecurityUtil.loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다"));

        Account account = user.getAccount();

        validateDepositRequest(request, account);

        account.deposit(request.getAmount());

        return TransactionDto.fromEntity(
                transactionRepository.save(
                        Transaction.createDeposit(
                                generateTxnNumber(),
                                request.getAmount(),
                                account
                        )
                )
        );
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @AccountLock(key = "#request.getAccountNumber()")
    public TransactionDto withdrawal(Withdrawal.Request request) {
        User user = userRepository.findById(SecurityUtil.loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다"));

        Account account = user.getAccount();

        validateWithdrawalRequest(request, account);

        account.withdrawal(request.getAmount());

        return TransactionDto.fromEntity(
                transactionRepository.save(
                        Transaction.createWithdrawal(
                                generateTxnNumber(),
                                request.getAmount(),
                                account
                        )
                )
        );
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionDto transfer(Transfer.Request request) {
        User user = userRepository.findById(loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다"));
        Account senderAccount = user.getAccount();

        String receiver = request.getReceiver();
        UserDto receiverUserDto = accountUserSearchService.search(receiver);
        User receiverUser = userRepository.findById(receiverUserDto.getId())
                .orElseThrow(() -> new NotFoundException("수취자가 존재하지 않습니다"));
        Account receiverAccount = receiverUser.getAccount();

        validateTransferRequest(request, senderAccount, receiverAccount);

        return null;
    }

    private void validateTransferRequest(Transfer.Request request, Account senderAccount, Account receiverAccount) {

    }

    private void validateWithdrawalRequest(Withdrawal.Request request, Account account) {
        if (ValidUtil.hasNotTexts(request.getAccountNumber(), request.getAccountPassword())) {
            // 계좌번호 및 계좌비밀번호를 입력하지 않은 경우
            throw new BadRequestException("계좌번호 및 계좌비밀번호를 모두 입력해주세요");
        }
        if (ValidUtil.isLessThanEqualsToZero(request.getAmount())) {
            // 출금액이 0보다 작거나 같은 경우
            throw new BadRequestException("출금액을 1원 이상 입력해주세요");
        }
        if (ValidUtil.isNull(account)) {
            // 유저가 계좌를 보유하고 있지 않은 경우
            throw new NotFoundException("계좌를 보유하고 있지 않습니다");
        }
        if (ValidUtil.isMismatch(request.getAccountNumber(), account.getNumber())) {
            // 요청 계좌번호와 유저 계좌의 계좌번호가 일치하지 않는 경우
            throw new ForbiddenException("계좌번호가 일치하지 않습니다");
        }
        if (passwordEncoderService.mismatch(request.getAccountPassword(), account.getPassword())) {
            // 요청 계좌비밀번호와 유저 계좌의 계좌비밀번호가 일치하지 않는 경우
            throw new ForbiddenException("계좌비밀번호가 일치하지 않습니다");
        }
        if (account.cannotWithdrawal(request.getAmount())) {
            // 계좌 잔액이 출금액보다 적은 경우
            throw new BadRequestException("잔액이 부족합니다");
        }
    }

    private String generateTxnNumber() {
        AtomicInteger count = new AtomicInteger();
        while (count.get() < 100) {
            String newTxnNumber = Generator.generateTxnNumber();
            if (!transactionRepository.existsByNumber(newTxnNumber)) {
                return newTxnNumber;
            }
            count.getAndIncrement();
        }
        throw new InternalServerException("거래번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요");
    }

    private void validateDepositRequest(Deposit.Request request, Account account) {
        if (ValidUtil.hasNotTexts(request.getAccountNumber())) {
            // 계좌번호를 입력하지 않은 경우
            throw new BadRequestException("계좌번호를 입력해주세요");
        }
        if (ValidUtil.isLessThanEqualsToZero(request.getAmount())) {
            // 입금액이 0보다 작거나 같은 경우
            throw new BadRequestException("입금액을 1원 이상 입력해주세요");
        }
        if (ValidUtil.isNull(account)) {
            // 유저가 계좌를 보유하고 있지 않은 경우
            throw new NotFoundException("계좌를 보유하고 있지 않습니다");
        }
        if (ValidUtil.isMismatch(request.getAccountNumber(), account.getNumber())) {
            // 요청 계좌번호와 계좌의 계좌번호가 일치하지 않는 경우
            throw new ForbiddenException("계좌번호가 일치하지 않습니다");
        }
    }

    private boolean isPhoneNumber(String keyword) {
        if (hasNotTexts(keyword)) {
            // 수취자 정보를 입력하지 않은 경우
            throw new BadRequestException("수취자 정보를 입력해주세요");
        }
        if (keyword.startsWith("010")) {
            if (isInvalidPhoneNumPattern(keyword)) {
                // 휴대폰번호가 올바른 패턴이 아닌 경우
                throw new BadRequestException("올바른 휴대폰번호로 입력해주세요");
            }
            return true;
        }
        return false;
    }

}
