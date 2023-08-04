package com.myfin.core.repository;

import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByOwner(User owner);

    boolean existsByNumber(String number);

    @Query(value = "SELECT a FROM account a WHERE a.deletedAt = null ")
    Optional<Account> findByNumber(String number);
}
