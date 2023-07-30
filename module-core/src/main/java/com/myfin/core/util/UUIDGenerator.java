package com.myfin.core.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {
    private UUIDGenerator() {}

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generate();
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
