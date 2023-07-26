package com.myfin.api.dto;

import java.time.LocalDateTime;

public class TopResponse {

    protected static String getDateTimeIfPresent(LocalDateTime t) {
        return t != null ? t.toString() : null;
    }

}
