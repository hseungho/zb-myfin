package com.myfin.redis.repository;

import com.myfin.redis.entity.CacheVerified;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheVerifiedRepository extends CrudRepository<CacheVerified, String> {
}
