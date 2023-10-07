package com.myfin.api.service.impl;

import com.myfin.api.service.AccountUserSearchService;
import com.myfin.core.dto.AccountDto;
import com.myfin.core.dto.UserDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.util.ValidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUserSearchServiceImpl implements AccountUserSearchService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto search(String keyword) {
        User searchedUser;

        if (isPhoneNumber(keyword)) {
            searchedUser = userRepository.findByPhoneNum(keyword)
                    .orElse(null);
            if (searchedUser == null || searchedUser.getAccount() == null) return null;
        } else {
            Account account = accountRepository.findByNumber(keyword)
                    .orElse(null);
            if (account == null) return null;
            searchedUser = account.getOwner();
        }

        return UserDto.fromEntity(searchedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto searchAccount(String keyword) {
        Account account;

        if (isPhoneNumber(keyword)) {
            User searchedUser = userRepository.findByPhoneNum(keyword)
                    .orElse(null);
            if (searchedUser == null || searchedUser.getAccount() == null) return null;
            account = searchedUser.getAccount();
        } else {
            account = accountRepository.findByNumber(keyword)
                    .orElse(null);
            if (account == null) return null;
        }

        return AccountDto.fromEntity(account);
    }

    private boolean isPhoneNumber(String keyword) {
        if (ValidUtil.hasNotTexts(keyword)) {
            // 키워드를 입력하지 않은 경우
            throw new BadRequestException("검색할 키워드를 입력해주세요");
        }
        if (keyword.startsWith("010")) {
            if (ValidUtil.isInvalidPhoneNumPattern(keyword)) {
                // 휴대폰번호가 올바른 패턴이 아닌 경우
                throw new BadRequestException("올바른 휴대폰번호로 입력해주세요");
            }
            return true;
        }
        return false;
    }
}
