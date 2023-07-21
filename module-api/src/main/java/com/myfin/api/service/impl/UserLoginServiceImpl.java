package com.myfin.api.service.impl;

import com.myfin.api.dto.TokenDto;
import com.myfin.api.service.ATopServiceComponent;
import com.myfin.api.service.UserLoginService;
import com.myfin.core.entity.User;
import com.myfin.core.exception.impl.BadRequestException;
import com.myfin.core.exception.impl.NotFoundException;
import com.myfin.core.exception.impl.UnauthorizedException;
import com.myfin.core.repository.UserRepository;
import com.myfin.security.jwt.JwtComponent;
import com.myfin.security.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl extends ATopServiceComponent implements UserLoginService {

    private final UserRepository userRepository;

    private final PasswordEncoderService passwordEncoderService;
    private final JwtComponent jwtComponent;

    @Override
    @Transactional
    public TokenDto login(String userId, String password) {
        // 요청 검증
        validateLoginRequest(userId, password);

        // 유저 정보 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다"));

        // 로그인 검증
        validateLoginUser(user, password);

        // 로그인일시 업데이트
        user.login();

        // 토큰 발행 및 응답
        return TokenDto.builder()
                .accessToken(jwtComponent.generateAccessToken(user.getId(), user.getType().name()))
                .refreshToken(jwtComponent.generateRefreshToken(user.getId(), user.getType().name()))
                .lastLoggedInAt(user.getLastLoggedInAt())
                .build();
    }

    private void validateLoginUser(User user, String password) {
        if (passwordEncoderService.mismatch(password, user.getPassword())) {
            // 로그인 시 유저패스워드가 불일치한 경우
            throw new UnauthorizedException("잘못된 비밀번호입니다");
        }
        if (user.isResigned()) {
            // 해당 유저가 이미 탈퇴한 유저인 경우
            throw new NotFoundException("이미 탈퇴된 유저입니다");
        }
    }

    private void validateLoginRequest(String userId, String password) {
        if (hasNotTexts(userId, password)) {
            // 로그인을 위한 필수 정보를 요청하지 않은 경우
            throw new BadRequestException("아이디와 비밀번호를 모두 입력해주세요");
        }
    }
}
