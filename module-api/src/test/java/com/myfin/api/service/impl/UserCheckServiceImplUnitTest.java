package com.myfin.api.service.impl;

import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserCheckServiceImplUnitTest {

    @InjectMocks
    private UserCheckServiceImpl userCheckService;
    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("아이디 사용가능여부 확인 - 사용가능")
    void test_checkUserIdAvailable_result_will_be_true() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(false);
        // when
        boolean result = userCheckService.checkUserIdAvailable("user-id");
        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("아이디 사용가능여부 확인 - 사용불가")
    void test_checkUserIdAvailable_result_will_be_false() {
        // given
        given(userRepository.existsByUserId(anyString()))
                .willReturn(true);
        // when
        boolean result = userCheckService.checkUserIdAvailable("user-id");
        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("아이디 사용가능여부 확인 - blank & null")
    void test_checkUserIdAvailable_request_is_blank() {
        // given
        // when
        BadRequestException ex_blank = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.checkUserIdAvailable("")
        );
        BadRequestException ex_null = Assertions.assertThrows(
                BadRequestException.class,
                () -> userCheckService.checkUserIdAvailable(null)
        );
        // then
        Assertions.assertEquals(400, ex_blank.getHttpStatus());
        Assertions.assertEquals("중복확인할 아이디를 입력해주세요.", ex_blank.getErrorMessage());
        Assertions.assertEquals(400, ex_null.getHttpStatus());
        Assertions.assertEquals("중복확인할 아이디를 입력해주세요.", ex_null.getErrorMessage());

    }

}