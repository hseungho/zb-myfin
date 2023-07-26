package com.myfin.redis.repository;

import com.myfin.redis.entity.CacheVerifyCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheVerifyCodeRepository extends CrudRepository<CacheVerifyCode, String> {
}
