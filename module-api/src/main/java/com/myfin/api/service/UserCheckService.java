package com.myfin.api.service;

public interface UserCheckService {

    /**
     * 아이디 사용가능 여부 확인.
     * @param userId 확인할 유저 아이디
     * @return 사용가능하면 true
     */
    boolean checkUserIdAvailable(String userId);

}
