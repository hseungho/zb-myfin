package com.myfin.core.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private Generator() {}

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

    public static String generateTxnNumber() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 20; i++) {
            if (random.nextBoolean()) {
                sb.append((char)(random.nextInt(26) + 65));
            } else {
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }

}
