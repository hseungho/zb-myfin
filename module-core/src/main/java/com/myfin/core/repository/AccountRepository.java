package com.myfin.core.repository;

import com.myfin.core.entity.Account;
import com.myfin.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByOwner(User owner);

    boolean existsByNumber(String number);

}
