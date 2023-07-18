package com.myfin.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class SeoulDateTime {

    /**
     * LocalDateTime 의 Zone 을 Asia/Seoul 로 지정하여 현재 시간을 반환하는 메소드.
     * @return Asia/Seoul 로 지정된 현재 시간 LocalDateTime 객체
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
