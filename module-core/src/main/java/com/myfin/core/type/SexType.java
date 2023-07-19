package com.myfin.core.type;

public enum SexType {
    MALE, FEMALE;

    public static SexType of(boolean sex) {
        return sex ? FEMALE : MALE;
    }
}
