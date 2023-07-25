package com.myfin.api.controller;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.dto.DeleteAccount;
import com.myfin.api.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request) {
        return CreateAccount.Response.fromDto(
                accountService.createAccount(request)
        );
    }

    @DeleteMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public DeleteAccount.Response deleteAccount(@RequestBody @Valid DeleteAccount.Request request) {
        return DeleteAccount.Response.fromDto();
    }

}
