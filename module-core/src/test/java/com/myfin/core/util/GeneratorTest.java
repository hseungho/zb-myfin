package com.myfin.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    @Test
    @DisplayName("계좌번호 생성기 테스트 - 1000만번동안 계좌번호에 010이 안포함되는지 확인")
    void test_generateAccountNumber() {
        // given
        // when
        // then
        for (int i = 0; i < 10_000_000; i++) {
            String accountNumber = Generator.generateAccountNumber();
            assertEquals(14, accountNumber.length());
            assertFalse(accountNumber.startsWith("010"));
        }
    }

}