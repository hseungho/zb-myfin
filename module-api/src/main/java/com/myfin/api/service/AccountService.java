package com.myfin.api.service;

import com.myfin.api.dto.CreateAccount;
import com.myfin.core.dto.AccountDto;

public interface AccountService {

    /**
     * 계좌 생성.
     * @param request 계좌 생성 요청 DTO 클래스
     * @return 계좌 DTO 클래스
     */
    AccountDto createAccount(CreateAccount.Request request);

}
