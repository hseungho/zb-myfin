package com.myfin.api.service.impl;

import com.myfin.api.service.UserCheckService;

public class UserCheckServiceImpl implements UserCheckService {
    @Override
    public boolean checkUserIdAvailable(String userId) {
        return false;
    }
}
