package com.myfin.api.controller;

import com.myfin.api.dto.CreateAccount;
import com.myfin.api.dto.DeleteAccount;
import com.myfin.api.dto.FindMyAccount;
import com.myfin.api.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request) {
        return CreateAccount.Response.fromDto(
                accountService.createAccount(request)
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public DeleteAccount.Response deleteAccount(@RequestBody @Valid DeleteAccount.Request request) {
        return DeleteAccount.Response.fromDto(
                accountService.deleteAccount(request)
        );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public FindMyAccount.Response findMyAccount(@RequestBody @Valid FindMyAccount.Request request) {
        return FindMyAccount.Response.fromDto(
                accountService.findMyAccount(request)
        );
    }

}
