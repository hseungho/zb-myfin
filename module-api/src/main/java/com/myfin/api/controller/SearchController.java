package com.myfin.api.controller;

import com.myfin.api.dto.SearchAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchController {

    @GetMapping("/search/accounts")
    @ResponseStatus(HttpStatus.OK)
    public SearchAccount.Response searchAccount(@RequestParam("param") final String param) {
        return SearchAccount.Response.fromDto();
    }

}
