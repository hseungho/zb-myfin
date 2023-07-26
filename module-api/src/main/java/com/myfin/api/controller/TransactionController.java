package com.myfin.api.controller;

import com.myfin.api.dto.Deposit;
import com.myfin.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public Deposit.Response deposit(@RequestBody @Valid Deposit.Request request) {
        return Deposit.Response.fromDto(
                transactionService.deposit(request)
        );
    }

}