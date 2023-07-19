package com.myfin.api.controller;

import com.myfin.api.dto.CheckIdAvailable;
import com.myfin.api.dto.SignUp;
import com.myfin.api.dto.VerifyIdentity;
import com.myfin.api.dto.VerifyRequestIdentity;
import com.myfin.api.service.UserSignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserSignUpService userSignUpService;

    @GetMapping("/sign-up/check-id")
    @ResponseStatus(HttpStatus.OK)
    public CheckIdAvailable.Response checkUserIdAvailable(@RequestParam("key") String userId) {
        return CheckIdAvailable.Response.of(
                userSignUpService.checkUserIdAvailable(userId)
        );
    }

    @PostMapping("/sign-up/verify/request")
    @ResponseStatus(HttpStatus.OK)
    public VerifyRequestIdentity.Response sendPhoneMessageForVerifyingIdentity(
            @RequestBody @Valid VerifyRequestIdentity.Request request) {

        return VerifyRequestIdentity.Response.of(
                userSignUpService.sendPhoneMessageForVerifyingIdentity(request.getPhoneNum())
        );
    }

    @PostMapping("/sign-up/verify")
    @ResponseStatus(HttpStatus.OK)
    public VerifyIdentity.Response verifyIdentity(
            @RequestBody @Valid VerifyIdentity.Request request) {

        return VerifyIdentity.Response.fromDto(
                userSignUpService.verifyIdentity(request)
        );
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public SignUp.Response signUp(@RequestBody @Valid SignUp.Request request) {
        return SignUp.Response.fromDto(
                userSignUpService.signUp(request)
        );
    }

}
