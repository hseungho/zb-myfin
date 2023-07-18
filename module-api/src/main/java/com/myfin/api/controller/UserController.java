package com.myfin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/sign-up/check-id")
    @ResponseStatus(HttpStatus.OK)
    public CheckIdAvailable.Response checkUserIdAvailable(@RequestParam("key") String userId) {
        return CheckIdAvailable.Response.of();
    }

}
