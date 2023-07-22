package com.myfin.api.config.filter;

import com.myfin.api.service.UserAuthenticationService;
import com.myfin.security.jwt.JwtComponent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.myfin.security.jwt.JwtComponent.TOKEN_HEADER;

/**
 * JwtAuthenticationFilter 는 요청이 Spring DispatchServlet 에 들어가기 이전에 <br>
 * 요청 헤더 중 Authorization 의 값인 JWT 토큰 값을 파싱하여 검증하는 필터 클래스.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtComponent jwtComponent;
    private final UserAuthenticationService userAuthenticationService;

    /**
     * JWT 토큰을 파싱하여 검증하는 메소드. <br>
     * JWT 토큰에 해당하는 유저 정보를 DB 에서 패치한 뒤, 해당 유저가 탈퇴된 유저인지를 검증한다. <br>
     * 탈퇴된 유저가 아니라면 SecurityHolder 에 Authentication 으로 해당 유저를 설정한다.
     * @param request Http 요청 객체
     * @param response Http 요청에 대한 응답 객체
     * @param filterChain Spring 의 FilterChain 인터페이스
     * @throws ServletException Servlet 처리 중 발생할 수 있음
     * @throws IOException IO 처리 중 발생할 수 있음
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = this.resolveTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtComponent.validateToken(token)) {
            Authentication authentication = userAuthenticationService.getAuthentication(jwtComponent.getId(token));

//            if (((User) authentication.getPrincipal()).isResigned()) { // JWT 토큰 유저가 탈퇴된 유저인지 확인
//                throw new UnauthorizedException(ErrorCodeType.UNAUTHORIZED_LOGIN_ALREADY_RESIGNED_USER);
//            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더 중 Authorization 값을 토큰 전달자인 Bearer Prefix 를 분리시킨 뒤, <br>
     * JWT 토큰 값만 반환하는 메소드.
     * @param request Http 요청 객체
     * @return JWT 토큰
     */
    private String resolveTokenFromRequest(HttpServletRequest request) {
        return jwtComponent.separatePrefix(request.getHeader(TOKEN_HEADER));
    }
}
