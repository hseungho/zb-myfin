package com.myfin.cache.repository;

import com.myfin.cache.entity.CacheVerifyCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheVerifyCodeRepository extends CrudRepository<CacheVerifyCode, String> {

    Optional<CacheVerifyCode> findByPhoneNum(String phoneNum);

}
