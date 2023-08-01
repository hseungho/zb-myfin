package com.myfin.api.service.impl

import com.myfin.api.dto.Deposit
import com.myfin.api.dto.Withdrawal
import com.myfin.api.mock.MockFactory
import com.myfin.api.mock.TestSecurityHolder
import com.myfin.api.service.AccountUserSearchService
import com.myfin.core.dto.TransactionDto
import com.myfin.core.exception.impl.BadRequestException
import com.myfin.core.exception.impl.ForbiddenException
import com.myfin.core.exception.impl.InternalServerException
import com.myfin.core.exception.impl.NotFoundException
import com.myfin.core.repository.AccountRepository
import com.myfin.core.repository.TransactionRepository
import com.myfin.core.repository.UserRepository
import com.myfin.core.type.TransactionType
import com.myfin.core.type.UserType
import com.myfin.core.util.SeoulDateTime
import com.myfin.security.service.PasswordEncoderService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class TransactionServiceImplUnitTest {

    private val userRepository = mockk<UserRepository>()
    private val accountRepository = mockk<AccountRepository>()
    private val transactionRepository = mockk<TransactionRepository>()
    private val passwordEncoderService = mockk<PasswordEncoderService>()
    private val service = TransactionServiceImpl(
        userRepository,
        accountRepository,
        transactionRepository,
        passwordEncoderService
    )

    @Test
    @DisplayName("계좌 입금 - 성공")
    fun test_deposit_success() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 0L, now, now, null)
        val transaction = MockFactory.mock_transaction_for_deposit_withdrawal(account, 1000L, TransactionType.DEPOSIT, now)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { transactionRepository.existsByNumber(any()) } returns false
        every { transactionRepository.save(any()) } returns transaction

        // when
        val result: TransactionDto = service.deposit(Deposit.Request.builder()
            .accountNumber(account.number).amount(1000L).build())

        // then
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("txn_number", result.number)
        assertEquals(1000L, result.amount)
        assertEquals(TransactionType.DEPOSIT, result.type)
        assertEquals(now, result.tradedAt)
        assertEquals(account.id, result.sender.id)
        assertEquals(account.number, result.sender.number)
        assertEquals(account.number, result.receiver.number)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 유저 조회 실패")
    fun test_deposit_failed_when_userNotFound() {
        // given
        TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER))
        every { userRepository.findByIdOrNull(any()) } returns null
        // when
        val ex = assertThrows(NotFoundException::class.java) {
            service.deposit(Deposit.Request.builder().build())
        }
        // then
        assertEquals(404, ex.httpStatus)
        assertEquals("존재하지 않는 유저입니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 계좌번호 없음")
    fun test_deposit_failed_when_hasNotAccountNumber() {
        // given
        val now = SeoulDateTime.now()

        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(BadRequestException::class.java) {
            service.deposit(Deposit.Request.builder().accountNumber(null).build())
        }
        // then
        assertEquals(400, ex.httpStatus)
        assertEquals("계좌번호를 입력해주세요", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 입금액 0원 이하")
    fun test_deposit_failed_when_amountIsLessThanEqualsToZero() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(BadRequestException::class.java) {
            service.deposit(Deposit.Request.builder()
                .accountNumber("12341234").amount(0L).build())
        }
        // then
        assertEquals(400, ex.httpStatus)
        assertEquals("입금액을 1원 이상 입력해주세요", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 유저 보유계좌 없음")
    fun test_deposit_failed_when_accountIsNull() {
        // given
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER))
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(NotFoundException::class.java) {
            service.deposit(Deposit.Request.builder()
                .accountNumber("12341234").amount(1000L).build())
        }
        // then
        assertEquals(404, ex.httpStatus)
        assertEquals("계좌를 보유하고 있지 않습니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 계좌번호 불일치")
    fun test_deposit_failed_when_misMatchAccountNumber() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns  user
        // when
        val ex = assertThrows(ForbiddenException::class.java) {
            service.deposit(Deposit.Request.builder()
                .accountNumber("wrong_account_number").amount(1000L).build())
        }
        // then
        assertEquals(403, ex.httpStatus)
        assertEquals("계좌번호가 일치하지 않습니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 입금 - 실패 - 거래번호 생성 실패")
    fun test_deposit_failed_when_errorByGenerateTxnNumber() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { transactionRepository.existsByNumber(any()) } returns true
        // when
        val ex = assertThrows(InternalServerException::class.java) {
            service.deposit(Deposit.Request.builder()
                .accountNumber(account.number).amount(1000L).build())
        }
        // then
        assertEquals(500, ex.httpStatus)
        assertEquals("거래번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 성공")
    fun test_withdrawal_success() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 10000L, now, now, null)
        val transaction = MockFactory.mock_transaction_for_deposit_withdrawal(account, 1000L, TransactionType.WITHDRAWAL, now)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { passwordEncoderService.mismatch(any(), any()) } returns false
        every { transactionRepository.existsByNumber(any()) } returns false
        every { transactionRepository.save(any()) } returns transaction
        // when
        val result: TransactionDto = service.withdrawal(Withdrawal.Request.builder()
            .accountNumber(account.number)
            .accountPassword(account.password)
            .amount(1000L).build())
        // then
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("txn_number", result.number)
        assertEquals(1000L, result.amount)
        assertEquals(TransactionType.WITHDRAWAL, result.type)
        assertEquals(now, result.tradedAt)
        assertEquals(account.id, result.sender.id)
        assertEquals(account.number, result.sender.number)
        assertEquals(account.number, result.receiver.number)
        assertEquals(9000L, account.balance)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 계좌번호 및 계좌비밀번호 미입력")
    fun test_withdrawal_failed_when_hasNotAccNumberOrPassword() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val exAn = assertThrows(BadRequestException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber(null).accountPassword("acc").build())
        }
        val exAp = assertThrows(BadRequestException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber("1123123").accountPassword(null).build())
        }
        // then
        assertEquals(400, exAn.httpStatus)
        assertEquals("계좌번호 및 계좌비밀번호를 모두 입력해주세요", exAn.errorMessage)
        assertEquals(400, exAp.httpStatus)
        assertEquals("계좌번호 및 계좌비밀번호를 모두 입력해주세요", exAp.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 출금액 0원 이하")
    fun test_withdrawal_failed_when_amountIsLessThanEqualsToZero() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(BadRequestException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber("an").accountPassword("ap").amount(0L).build())
        }
        // then
        assertEquals(400, ex.httpStatus)
        assertEquals("출금액을 1원 이상 입력해주세요", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 유저 계좌 없음")
    fun test_withdrawal_failed_when_accountIsNull() {
        // given
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(NotFoundException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber("an").accountPassword("ap").amount(1000L).build())
        }
        // then
        assertEquals(404, ex.httpStatus)
        assertEquals("계좌를 보유하고 있지 않습니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 계좌번호 불일치")
    fun test_withdrawal_failed_when_misMatchAccNumber() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        MockFactory.mock_account(user, 0L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        // when
        val ex = assertThrows(ForbiddenException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber("wrong_an").accountPassword("ap").amount(1000L).build())
        }
        // then
        assertEquals(403, ex.httpStatus)
        assertEquals("계좌번호가 일치하지 않습니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 촐금 - 실패 - 계좌비밀번호 불일치")
    fun test_withdrawal_failed_when_misMatchAccPassword() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 10000L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { passwordEncoderService.mismatch(any(), any()) } returns true
        // when
        val ex = assertThrows(ForbiddenException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber(account.number).accountPassword("ap").amount(1000L).build())
        }
        // then
        assertEquals(403, ex.httpStatus)
        assertEquals("계좌비밀번호가 일치하지 않습니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 잔액 부족")
    fun test_withdrawal_failed_when_lowBalance() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 10000L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { passwordEncoderService.mismatch(any(), any())} returns false
        // when
        val ex = assertThrows(BadRequestException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber(account.number).accountPassword("ap").amount(100000L).build())
        }
        // then
        assertEquals(400, ex.httpStatus)
        assertEquals("잔액이 부족합니다", ex.errorMessage)
    }

    @Test
    @DisplayName("계좌 출금 - 실패 - 거래번호 생성 실패")
    fun test_withdrawal_failed_when_errorByGenerateTxnNumber() {
        // given
        val now = SeoulDateTime.now()
        val user = TestSecurityHolder.setSecurityHolderUser(MockFactory.mock_user(UserType.ROLE_USER, null, null, null))
        val account = MockFactory.mock_account(user, 10000L, now, now, null)
        every { userRepository.findByIdOrNull(any()) } returns user
        every { transactionRepository.existsByNumber(any()) } returns true
        every { passwordEncoderService.mismatch(any(), any())} returns false
        // when
        val ex = assertThrows(InternalServerException::class.java) {
            service.withdrawal(Withdrawal.Request.builder()
                .accountNumber(account.number).accountPassword("ap").amount(1000L).build())
        }
        // then
        assertEquals(500, ex.httpStatus)
        assertEquals("거래번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요", ex.errorMessage)
    }

}