package com.myfin.api.service;

import com.myfin.core.dto.AccountDto;
import com.myfin.core.dto.UserDto;
import jakarta.annotation.Nullable;

public interface AccountUserSearchService {

    /**
     * 계좌번호 또는 휴대폰 번호를 이용하여 계좌 검색.
     * @param keyword 계좌번호 또는 휴대폰번호
     * @return 유저 DTO 클래스. 만일 리턴값이 null이라면 조회되지 않았음을 의미
     */
    @Nullable
    UserDto search(String keyword);

    /**
     * 계좌번호 또는 휴대폰번호를 이용하여 계좌 검색.
     * @param keyword 계좌번호 또는 휴대폰번호
     * @return 계좌 DTO 클래스. 만일 리턴값이 null이라면 조회되지 않았음을 의미
     */
    @Nullable
    AccountDto searchAccount(String keyword);
}
