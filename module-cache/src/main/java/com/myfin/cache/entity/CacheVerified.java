package com.myfin.cache.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Getter
@RedisHash(value = "Verified", timeToLive = 600L) // TTL: 10분
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class CacheVerified {

    @Id
    private String phoneNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheVerified that = (CacheVerified) o;
        return Objects.equals(phoneNum, that.phoneNum);
    }

    @Override
    public int hashCode() {
        return phoneNum != null ? phoneNum.hashCode() : 0;
    }

}
