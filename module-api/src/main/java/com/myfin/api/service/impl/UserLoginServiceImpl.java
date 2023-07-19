package com.myfin.api.service.impl;

import com.myfin.api.dto.TokenDto;
import com.myfin.api.service.UserLoginService;
import com.myfin.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserRepository userRepository;

    @Override
    public TokenDto login(String userId, String password) {
        return null;
    }
}
