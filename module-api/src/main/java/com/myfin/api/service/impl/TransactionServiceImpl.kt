package com.myfin.api.service.impl

import com.myfin.api.dto.Deposit
import com.myfin.api.dto.Transfer
import com.myfin.api.dto.Withdrawal
import com.myfin.api.service.AccountUserSearchService
import com.myfin.api.service.TransactionService
import com.myfin.core.dto.TransactionDto
import com.myfin.core.entity.Account
import com.myfin.core.entity.Transaction
import com.myfin.core.exception.impl.BadRequestException
import com.myfin.core.exception.impl.ForbiddenException
import com.myfin.core.exception.impl.InternalServerException
import com.myfin.core.exception.impl.NotFoundException
import com.myfin.core.repository.TransactionRepository
import com.myfin.core.repository.UserRepository
import com.myfin.core.util.Generator
import com.myfin.core.util.SecurityUtil
import com.myfin.core.util.ValidUtil
import com.myfin.redis.lock.AccountLock
import com.myfin.security.service.PasswordEncoderService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicInteger

@Service
open class TransactionServiceImpl(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val passwordEncoderService: PasswordEncoderService,
    private val accountUserSearchService: AccountUserSearchService
) : TransactionService {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @AccountLock(key = "#request.getAccountNumber()")
    override fun deposit(request: Deposit.Request): TransactionDto {
        val account : Account = (userRepository.findByIdOrNull(SecurityUtil.loginId())
            ?: throw NotFoundException("존재하지 않는 유저입니다"))
            .account
            ?: throw NotFoundException("계좌를 보유하고 있지 않습니다")

        validateDepositRequest(request, account)

        account.addBalance(request.amount)

        return TransactionDto.fromEntity(
            transactionRepository.save(
                Transaction.createDeposit(
                    generateTxnNumber(),
                    request.amount,
                    account
                )
            )
        )
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @AccountLock(key = "#request.getAccountNumber()")
    override fun withdrawal(request: Withdrawal.Request): TransactionDto {
        val account : Account = (userRepository.findByIdOrNull(SecurityUtil.loginId())
            ?: throw NotFoundException("존재하지 않는 유저입니다"))
            .account
            ?: throw NotFoundException("계좌를 보유하고 있지 않습니다")

        validateWithdrawalRequest(request, account)

        account.useBalance(request.amount)

        return TransactionDto.fromEntity(
            transactionRepository.save(
                Transaction.createWithdrawal(
                    generateTxnNumber(),
                    request.amount,
                    account
                )
            )
        )
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    override fun transfer(request: Transfer.Request): TransactionDto {
        val senderAccount : Account = (userRepository.findByIdOrNull(SecurityUtil.loginId())
            ?: throw NotFoundException("존재하지 않는 유저입니다"))
            .account
            ?: throw NotFoundException("계좌를 보유하고 있지 않습니다")

        val receiverAccount = (accountUserSearchService.search(request.receiver)
            ?: throw NotFoundException("수취자가 존재하지 않습니다")).let {
                userRepository.findByIdOrNull(it.id)
                    ?: throw NotFoundException("수취자가 존재하지 않습니다")
            }.account
            ?: throw NotFoundException("계좌를 보유하고 있지 않습니다")

        validateTransferRequest(request, senderAccount, receiverAccount)

        senderAccount.useBalance(request.amount)
        receiverAccount.addBalance(request.amount)

        return TransactionDto.fromEntity(
            transactionRepository.save(
                Transaction.createTransfer(
                    generateTxnNumber(),
                    request.amount,
                    senderAccount,
                    receiverAccount
                )
            )
        )
    }

    private fun generateTxnNumber(): String {
        val count = AtomicInteger()
        while (count.get() < 100) {
            Generator.generateTxnNumber()?.let {
                if (!transactionRepository.existsByNumber(it)) {
                    return it
                }
            }
            count.getAndIncrement()
        }
        throw InternalServerException("거래번호 생성에 문제가 발생하였습니다. 관리자에게 문의해주세요")
    }

    private fun validateDepositRequest(request : Deposit.Request, account : Account) {
        if (ValidUtil.hasNotTexts(request.accountNumber)) {
            // 계좌번호를 입력하지 않은 경우
            throw BadRequestException("계좌번호를 입력해주세요")
        }
        if (ValidUtil.isLessThanEqualsToZero(request.amount)) {
            // 입금액이 0보다 작거나 같은 경우
            throw BadRequestException("입금액을 1원 이상 입력해주세요")
        }
        if (ValidUtil.isMismatch(request.accountNumber, account.number)) {
            // 요청 계좌번호와 계좌의 계좌번호가 일치하지 않는 경우
            throw ForbiddenException("계좌번호가 일치하지 않습니다")
        }
    }

    private fun validateWithdrawalRequest(request: Withdrawal.Request, account: Account) {
        if (ValidUtil.hasNotTexts(request.accountNumber, request.accountPassword)) {
            // 계좌번호 및 계좌비밀번호를 입력하지 않은 경우
            throw BadRequestException("계좌번호 및 계좌비밀번호를 모두 입력해주세요")
        }
        if (ValidUtil.isLessThanEqualsToZero(request.amount)) {
            // 출금액이 0보다 작거나 같은 경우
            throw BadRequestException("출금액을 1원 이상 입력해주세요")
        }
        if (ValidUtil.isMismatch(request.accountNumber, account.number)) {
            // 요청 계좌번호와 유저 계좌의 계좌번호가 일치하지 않는 경우
            throw ForbiddenException("계좌번호가 일치하지 않습니다")
        }
        if (passwordEncoderService.mismatch(request.accountPassword, account.password)) {
            // 요청 계좌비밀번호와 유저 계좌의 계좌비밀번호가 일치하지 않는 경우
            throw ForbiddenException("계좌비밀번호가 일치하지 않습니다")
        }
        if (account.isNotAvailableBalance(request.amount)) {
            // 계좌 잔액이 출금액보다 적은 경우
            throw BadRequestException("잔액이 부족합니다")
        }
    }

    private fun validateTransferRequest(request: Transfer.Request, senderAccount: Account, receiverAccount: Account) {
        if (ValidUtil.hasNotTexts(request.accountNumber, request.accountPassword, request.receiver)
            || ValidUtil.isNull(request.amount)) {
            // 필수 파라미터를 입력하지 않은 경우
            throw BadRequestException("이체에 필요한 모든 정보를 입력해주세요")
        }
        if (ValidUtil.isLessThanEqualsToZero(request.amount)) {
            // 송금액이 0원 이하인 경우
            throw BadRequestException("송금액을 1원 이상 입력해주세요")
        }
        if (ValidUtil.isMismatch(request.accountNumber, senderAccount.number)) {
            // 요청 계좌번호와 송금자 계좌번호가 일치하지 않은 경우
            throw ForbiddenException("계좌번호가 일치하지 않습니다")
        }
        if (passwordEncoderService.mismatch(request.accountPassword, senderAccount.password)) {
            // 요청 계좌비밀번호와 송금자 계좌의 계좌비밀번호가 일치하지 않은 경우
            throw ForbiddenException("계좌비밀번호가 일치하지 않습니다")
        }
        if (senderAccount.isNotAvailableBalance(request.amount)) {
            // 계좌의 잔액이 부족한 경우
            throw BadRequestException("잔액이 부족합니다")
        }
    }
}