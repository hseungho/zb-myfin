package com.myfin.api.controller;

import com.myfin.api.dto.CreateAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request) {
        return CreateAccount.Response.fromDto(null);
    }

}
