package com.adregamdi.core.jwt.service;

import com.adregamdi.core.exception.GlobalException;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.adregamdi.core.exception.GlobalException.LogoutMemberException;
import static com.adregamdi.member.exception.MemberException.MemberNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Getter
@Service
public class JwtService {
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String MEMBER_ID_CLAIM = "memberId";
    private static final String ROLE = "role";
    private static final String BEARER = "Bearer ";
    private final MemberRepository memberRepository;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    public String createAccessToken(final String memberId, final Role role) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + accessTokenExpirationPeriod);
        String token = JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(expiresAt)
                .withClaim(MEMBER_ID_CLAIM, memberId)
                .withClaim(ROLE, role.toString())
                .sign(Algorithm.HMAC512(secretKey));

        log.info("memberId로 액세스 토큰 생성: {}. 만료 기간: {}", memberId, expiresAt);
        return token;
    }

    public String createNoExpiresAtAccessToken(
            final String memberId,
            final Role role
    ) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withClaim(MEMBER_ID_CLAIM, memberId)
                .withClaim(ROLE, role.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + refreshTokenExpirationPeriod);
        String token = JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC512(secretKey));

        log.info("리프레쉬 토큰 생성. 만료 기간: {}", expiresAt);
        return token;
    }

    public void sendAccessToken(
            final HttpServletResponse response,
            final String accessToken
    ) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    public void sendAccessAndRefreshToken(
            final HttpServletResponse response,
            final String accessToken,
            final String refreshToken
    ) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    public Optional<String> extractAccessToken(final HttpServletRequest request) {
        Optional<String> token = Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));

        log.info("추출된 액세스 토큰: {}", token.isPresent() ? "존재" : "존재하지 않음");
        return token;
    }

    public Optional<String> extractRefreshToken(final HttpServletRequest request) {
        Optional<String> token = Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));

        log.info("추출된 리프레쉬 토큰: {}", token.isPresent() ? "존재" : "존재하지 않음");
        return token;
    }

    public Optional<String> extractMemberId(final String accessToken) {
        try {
            String memberId = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(MEMBER_ID_CLAIM)
                    .as(String.class);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            if (Boolean.FALSE.equals(member.getRefreshTokenStatus())) {
                log.info("{} 로그아웃 상태인 회원입니다. 유효하지 않은 토큰.", memberId);
                throw new LogoutMemberException();
            }

            log.info("액세스 토큰으로부터 추출된 memberId: {}", memberId);
            return Optional.of(memberId);
        } catch (JWTVerificationException e) {
            log.info("액세스 토큰으로부터 memberId 추출 실패. 에러: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void setAccessTokenHeader(
            final HttpServletResponse response,
            final String accessToken
    ) {
        response.setHeader(accessHeader, accessToken);
    }

    public void setRefreshTokenHeader(
            final HttpServletResponse response,
            final String refreshToken
    ) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public boolean isTokenValid(final String token) {
        if (token == null || token.trim().isEmpty()) {
            log.info("토큰이 비어있습니다.");
            throw new GlobalException.EmptyTokenException();
        }
        
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            log.info("유효한 토큰.");
            return true;
        } catch (GlobalException.TokenExpiredException e) {
            log.info("토큰이 만료되었습니다.");
            throw new GlobalException.TokenExpiredException();
        } catch (SignatureVerificationException e) {
            log.info("토큰 서명이 유효하지 않습니다.");
            throw new GlobalException.TokenValidationException("토큰 서명이 유효하지 않습니다.");
        } catch (JWTVerificationException e) {
            log.info("유효하지 않은 토큰. 에러: {}", e.getMessage());
            throw new GlobalException.TokenValidationException("유효하지 않은 토큰입니다: " + e.getMessage());
        }
    }
}
