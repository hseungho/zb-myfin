package com.myfin.cache.repository;

import com.myfin.cache.entity.CacheVerifyCode;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories
public interface CacheVerifyCodeRepository extends CrudRepository<CacheVerifyCode, String> {
}
