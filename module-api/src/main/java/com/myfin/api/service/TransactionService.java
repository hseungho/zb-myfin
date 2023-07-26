package com.myfin.api.service;

import com.myfin.api.dto.Deposit;
import com.myfin.core.dto.TransactionDto;

public interface TransactionService {

    /**
     * 계좌 입금.
     * @param request 계좌 입금 요청 DTO 클래스
     * @return 트랜잭션 DTO 클래스
     */
    TransactionDto deposit(Deposit.Request request);
}
