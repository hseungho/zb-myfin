package com.myfin.api.dto;

import java.time.LocalDateTime;

public class ATopResponse {

    protected static String getDateTimeIfPresent(LocalDateTime t) {
        return t != null ? t.toString() : null;
    }

}
