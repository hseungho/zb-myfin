package com.myfin.api.controller;

import com.myfin.api.dto.SearchAccount;
import com.myfin.api.service.AccountUserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchController {

    private final AccountUserSearchService accountUserSearchService;

    @GetMapping("/search/accounts")
    @ResponseStatus(HttpStatus.OK)
    public SearchAccount.Response searchAccount(@RequestParam(value = "keyword", required = true) final String keyword) {
        return SearchAccount.Response.fromDto(
                accountUserSearchService.search(keyword)
        );
    }

}
