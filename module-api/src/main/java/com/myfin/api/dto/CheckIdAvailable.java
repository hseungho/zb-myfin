package com.myfin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CheckIdAvailable {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    @Builder
    public static class Response {
        private boolean result;
    }

}
