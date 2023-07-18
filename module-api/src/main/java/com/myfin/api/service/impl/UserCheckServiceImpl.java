package com.myfin.api.service.impl;

import com.myfin.api.service.UserCheckService;
import com.myfin.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCheckServiceImpl implements UserCheckService {

    private final UserRepository userRepository;

    @Override
    public boolean checkUserIdAvailable(String userId) {
        return false;
    }

}
