package com.myfin.api.service;

import com.myfin.api.dto.TokenDto;

public interface UserLoginService {

    TokenDto login(String userId, String password);

}
