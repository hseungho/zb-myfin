package com.myfin.core.repository;

import com.myfin.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUserId(String userId);

    boolean existsByPhoneNum(String phoneNum);

    Optional<User> findByUserId(String userId);

}
