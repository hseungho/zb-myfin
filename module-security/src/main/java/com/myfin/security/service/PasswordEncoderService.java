package com.myfin.security.service;

public interface PasswordEncoderService {

    String encode(String rawPassword);

    boolean match(String rawPassword, String encodedPassword);

    boolean mismatch(String rawPassword, String encodedPassword);

}
