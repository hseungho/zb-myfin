package com.myfin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CheckIdAvailable {

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Response {
        private boolean result;
    }

}
