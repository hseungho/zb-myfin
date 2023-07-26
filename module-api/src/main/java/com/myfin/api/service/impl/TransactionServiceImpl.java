package com.myfin.api.service.impl;

import com.myfin.api.dto.Deposit;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends TopServiceComponent implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionDto deposit(Deposit.Request request) {
        User user = userRepository.findById(loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다"));

        Account account = user.getAccount();

        validateDepositRequest(request, account);

        deposit(request, account);

        return TransactionDto.fromEntity(
                transactionRepository.save(
                        Transaction.createDeposit(
                                generateTxnNumber(),
                                request.getAmount(),
                                account.getNumber(),
                                account
                        )
                )
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void deposit(Deposit.Request request, Account account) {
        account.deposit(request.getAmount());
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
        if (hasNotTexts(request.getAccountNumber())) {
            // 계좌번호를 입력하지 않은 경우
            throw new BadRequestException("계좌번호를 입력해주세요");
        }
        if (isLessThanEqualsToZero(request.getAmount())) {
            // 입금액이 0보다 작거나 같은 경우
            throw new BadRequestException("입금액을 1원 이상 입력해주세요");
        }
        if (account == null) {
            // 유저가 계좌를 보유하고 있지 않은 경우
            throw new NotFoundException("계좌를 보유하고 있지 않습니다");
        }
        if (isMismatch(request.getAccountNumber(), account.getNumber())) {
            // 요청 계좌번호와 계좌의 계좌번호가 일치하지 않는 경우
            throw new ForbiddenException("계좌번호가 일치하지 않습니다");
        }
    }
}