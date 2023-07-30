package com.myfin.api.service;

import com.myfin.api.dto.Deposit;
import com.myfin.api.dto.Transfer;
import com.myfin.api.dto.Withdrawal;
import com.myfin.core.dto.TransactionDto;

public interface TransactionService {

    /**
     * 계좌 입금.
     * @param request 계좌 입금 요청 DTO 클래스
     * @return 트랜잭션 DTO 클래스
     */
    TransactionDto deposit(Deposit.Request request);

    /**
     * 계좌 출금.
     * @param request 계좌 출금 요청 DTO 클래스
     * @return 트랜잭션 DTO 클래스
     */
    TransactionDto withdrawal(Withdrawal.Request request);

    /**
     * 송금
     * @param request 송금 요청 DTO 클래스
     * @return 트랜잭션 DTO 클래스
     */
    TransactionDto transfer(Transfer.Request request);
}
