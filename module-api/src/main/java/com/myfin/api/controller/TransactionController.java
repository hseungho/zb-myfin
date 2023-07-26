package com.myfin.api.controller;

import com.myfin.api.dto.Deposit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public Deposit.Response deposit(@RequestBody @Valid Deposit.Request request) {
        return Deposit.Response.fromDto();
    }

}
