package com.myfin.api.service;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.dto.DeleteAccount;
import com.myfin.api.dto.FindMyAccount;
import com.myfin.core.dto.AccountDto;

public interface AccountService {

    /**
     * 계좌 생성.
     * @param request 계좌 생성 요청 DTO 클래스
     * @return 계좌 DTO 클래스
     */
    AccountDto createAccount(CreateAccount.Request request);

    /**
     * 계좌 삭제.
     * @param request 계좌 삭제 요청 DTO 클래스
     * @return 계좌 DTO 클래스
     */
    AccountDto deleteAccount(DeleteAccount.Request request);

    /**
     * 내 계좌 조회.
     * @param request 계좌 조회 요청 DTO 클래스
     * @return 계좌 DTO 클래스
     */
    AccountDto findMyAccount(FindMyAccount.Request request);
}
