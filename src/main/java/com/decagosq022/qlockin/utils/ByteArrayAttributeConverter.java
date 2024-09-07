package com.decagosq022.qlockin.utils;

import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Base64;

@Converter(autoApply = true)
public class ByteArrayAttributeConverter implements AttributeConverter<ByteArray, String> {
    @Override
    public String convertToDatabaseColumn(ByteArray byteArray) {
        return byteArray == null ? null : byteArray.getBase64();
    }

    // this how you you collect it from the database
    @Override
    public ByteArray convertToEntityAttribute(String s) {
        return s == null ? null : new ByteArray(Base64.getDecoder().decode(s));
    }
}
