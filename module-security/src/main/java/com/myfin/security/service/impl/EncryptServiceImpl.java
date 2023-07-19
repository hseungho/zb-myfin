package com.myfin.security.service.impl;

import com.myfin.security.service.EncryptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptServiceImpl implements EncryptService {

    private final AesBytesEncryptor encryptor;

    @Override
    public String encrypt(String str) {
        return this.byteToString(encryptor.encrypt(str.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String decrypt(String encrypted) {
        return new String(encryptor.decrypt(stringToByteArray(encrypted)), StandardCharsets.UTF_8);
    }

    private String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b :bytes){
            sb.append(b);
            sb.append(" ");
        }
        return sb.toString();
    }

    private byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            buffer.put((byte) Integer.parseInt(s));
        }
        return buffer.array();
    }
}
