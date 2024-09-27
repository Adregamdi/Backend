package com.adregamdi.core.jwt.service;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
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
import static com.adregamdi.core.exception.GlobalException.TokenValidationException;
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

        log.info("Created Access Token for memberId: {}. Expires at: {}", memberId, expiresAt);
        return token;
    }

    public String createNoExpiresAtAccessToken(
            final String memberId,
            final Role role
    ) {
        Date now = new Date();
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

        log.info("Created Refresh Token. Expires at: {}", expiresAt);
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

        log.info("Extracted Access Token: {}", token.isPresent() ? "Present" : "Absent");
        return token;
    }

    public Optional<String> extractRefreshToken(final HttpServletRequest request) {
        Optional<String> token = Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));

        log.info("Extracted Refresh Token: {}", token.isPresent() ? "Present" : "Absent");
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
                log.info("Member {} has logged out. Token is invalid.", memberId);
                throw new LogoutMemberException();
            }

            log.info("Extracted memberId from Access Token: {}", memberId);
            return Optional.of(memberId);
        } catch (JWTVerificationException e) {
            log.info("Failed to extract memberId from Access Token. Error: {}", e.getMessage());
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
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            log.info("Token is valid");
            return true;
        } catch (JWTVerificationException e) {
            log.info("Token is invalid. Error: {}", e.getMessage());
            throw new TokenValidationException();
        }
    }
}
