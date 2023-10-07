package com.myfin.api.controller;

import com.myfin.api.dto.Deposit;
import com.myfin.api.dto.Transfer;
import com.myfin.api.dto.Withdrawal;
import com.myfin.api.service.AccountUserSearchService;
import com.myfin.api.service.TransactionService;
import com.myfin.core.dto.AccountDto;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.util.ValidUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountUserSearchService accountUserSearchService;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public Deposit.Response deposit(@RequestBody @Valid Deposit.Request request) {
        return Deposit.Response.fromDto(
                transactionService.deposit(request)
        );
    }

    @PostMapping("/withdrawal")
    @ResponseStatus(HttpStatus.OK)
    public Withdrawal.Response withdrawal(@RequestBody @Valid Withdrawal.Request request) {
        return Withdrawal.Response.fromDto(
                transactionService.withdrawal(request)
        );
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public Transfer.Response transfer(@RequestBody @Valid Transfer.Request request) {
        AccountDto receiverAccountDto = accountUserSearchService.searchAccount(request.getReceiver());
        if (ValidUtil.isNull(receiverAccountDto)) {
            throw new NotFoundException("수취자를 찾을 수 없습니다");
        }
        return Transfer.Response.fromDto(
                transactionService.transfer(
                        request.setReceiverAccountNumber(receiverAccountDto.getNumber()))
        );
    }

}
