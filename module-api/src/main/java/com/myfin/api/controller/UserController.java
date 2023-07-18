package com.myfin.api.controller;

import com.myfin.api.dto.CheckIdAvailable;
import com.myfin.api.dto.VerifyRequestIdentity;
import com.myfin.api.service.UserCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserCheckService userCheckService;

    @GetMapping("/sign-up/check-id")
    @ResponseStatus(HttpStatus.OK)
    public CheckIdAvailable.Response checkUserIdAvailable(@RequestParam("key") String userId) {
        return CheckIdAvailable.Response.of(
                userCheckService.checkUserIdAvailable(userId)
        );
    }

    @PostMapping("/sign-up/verify/request")
    @ResponseStatus(HttpStatus.OK)
    public VerifyRequestIdentity.Response sendPhoneMessageForVerifyingIdentity(
            @RequestBody @Valid VerifyRequestIdentity.Request request) {
        return VerifyRequestIdentity.Response.of(
                userCheckService.sendPhoneMessageForVerifyingIdentity(request.getPhoneNum())
        );
    }

}
