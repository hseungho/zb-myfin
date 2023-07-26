package com.myfin.core.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    public static String generateVerifyCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public static String generateAccountNumber() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
