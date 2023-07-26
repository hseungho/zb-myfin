package com.myfin.core.util;

import com.myfin.security.service.EncryptService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class EncryptConverter implements AttributeConverter<String, String> {

    private final EncryptService encryptService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptService.decrypt(dbData);
    }
}
