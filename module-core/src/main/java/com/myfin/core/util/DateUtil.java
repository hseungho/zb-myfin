package com.myfin.core.util;

import java.time.LocalDateTime;

public class DateUtil {
    private DateUtil() {}


    public static String getDateTimeIfPresent(LocalDateTime t) {
        return t != null ? t.toString() : null;
    }

}
