package com.myfin.api.service.impl;

import com.myfin.api.mock.MockFactory;
import com.myfin.core.dto.UserDto;
import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.AccountRepository;
import com.myfin.core.repository.UserRepository;
import com.myfin.core.type.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountUserSearchServiceImplUnitTest {

    @InjectMocks private AccountUserSearchServiceImpl service;

    @Mock private UserRepository userRepository;

    @Mock private AccountRepository accountRepository;

    @Test
    @DisplayName("계좌 검색 - 성공 - 계좌번호 검색")
    void test_search_success_when_keywordIsAccountNumber() {
        // given
        User owner = MockFactory.mock_user(UserType.ROLE_USER);
        Account account = MockFactory.mock_account(owner, 10000L);
        given(accountRepository.findByNumber(anyString()))
                .willReturn(Optional.of(account));
        // when
        UserDto result = service.search("123412341234");
        // then
        assertEquals(owner.getName(), result.getName());
    }

    @Test
    @DisplayName("계좌 검색 - 성공 - 휴대폰번호 검색")
    void test_search_success_when_keywordIsPhoneNum() {
        // given
        User owner = MockFactory.mock_user(UserType.ROLE_USER);
        MockFactory.mock_account(owner, 10000L);
        given(userRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.of(owner));
        // when
        UserDto result = service.search("01012341234");
        // then
        assertEquals(owner.getName(), result.getName());
    }

    @Test
    @DisplayName("계좌 검색 - 성공 - 계좌번호에 해당하는 계좌 없음")
    void test_search_success_when_keyIsAccNum_noResultAccount() {
        // given
        given(accountRepository.findByNumber(anyString()))
                .willReturn(Optional.empty());
        // when
        UserDto result = service.search("123412341234");
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("계좌 검색 - 성공 - 휴대폰번호에 해당하는 유저는 존재하지만 유저의 계좌가 없는 경우")
    void test_search_success_when_keyIsPhone_noResultAccount() {
        // given
        User owner = MockFactory.mock_user(UserType.ROLE_USER);
        given(userRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.of(owner));
        // when
        UserDto result = service.search("01012341234");
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("계좌 검색 - 성공 - 휴대폰번호에 해당하는 유저가 존재하지 않은 경우")
    void test_search_success_when_keyIsPhone_noResultUser() {
        // given
        given(userRepository.findByPhoneNum(anyString()))
                .willReturn(Optional.empty());
        // when
        UserDto result = service.search("01012341234");
        // then
        assertNull(result);
    }

    @Test
    @DisplayName("계좌 검색 - 실패 - 키워드 미제공")
    void test_search_failed_when_hasNotKeyword() {
        // given
        // when
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.search(null)
        );
        // then
        assertEquals(400, ex.getHttpStatus());
        assertEquals("검색할 키워드를 입력해주세요", ex.getErrorMessage());
    }

    @Test
    @DisplayName("계좌 검색 - 실패 - 유효하지 않은 휴대폰번호 패턴")
    void test_search_failed_when_invalidPhonePattern() {
        // given
        // when
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.search("010123412345")
        );
        // then
        assertEquals(400, ex.getHttpStatus());
        assertEquals("올바른 휴대폰번호로 입력해주세요", ex.getErrorMessage());
    }
}