package com.myfin.core.util;

import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    public static String generateVerifyCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

}
