package com.myfin.cache.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Getter
@RedisHash(value = "VerifyCode", timeToLive = 180L) // TTL: 3분
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class CacheVerifyCode {

    @Id
    private String phoneNum;

    private String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheVerifyCode that = (CacheVerifyCode) o;
        return Objects.equals(phoneNum, that.phoneNum);
    }

    @Override
    public int hashCode() {
        return phoneNum != null ? phoneNum.hashCode() : 0;
    }

}
