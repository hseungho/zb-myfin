package com.myfin.api.service.impl;

import com.myfin.api.service.ATopServiceComponent;
import com.myfin.api.service.UserCheckService;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCheckServiceImpl extends ATopServiceComponent implements UserCheckService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserIdAvailable(String userId) {
        if (hasNotTexts(userId)) {
            throw new BadRequestException("중복확인할 아이디를 입력해주세요.");
        }
        return !userRepository.existsByUserId(userId);
    }

}
