package com.myfin.api.service.impl;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.service.TopServiceComponent;
import com.myfin.api.service.AccountService;
import com.myfin.core.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends TopServiceComponent implements AccountService {

    @Override
    public AccountDto createAccount(CreateAccount.Request request) {
        return null;
    }

}
