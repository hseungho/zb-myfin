package com.myfin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

public class CheckIdAvailable {

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Response {
        @NotBlank
        private boolean result;
    }

}
