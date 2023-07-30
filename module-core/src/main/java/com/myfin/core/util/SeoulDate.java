package com.myfin.core.util;

import java.time.LocalDate;
import java.time.ZoneId;

public class SeoulDate {
    private SeoulDate() {}

    /**
     * LocalDate 의 Zone 을 Asia/Seoul 로 지정하여 현재 일자를 반환
     * @return Asia/Seoul 로 지정된 현재 일자 LocalDate 객체
     */
    public static LocalDate now() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

}
