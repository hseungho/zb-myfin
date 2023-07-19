package com.myfin.security.service;

public interface EncryptService {

    String encrypt(String str);

    String decrypt(String encrypted);

}
