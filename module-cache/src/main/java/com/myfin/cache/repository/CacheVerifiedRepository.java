package com.myfin.cache.repository;

import com.myfin.cache.entity.CacheVerified;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheVerifiedRepository extends CrudRepository<CacheVerified, String> {
}
