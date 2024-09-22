package com.adregamdi.core.oauth2.handler;

import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.domain.CustomOAuth2User;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        log.info("OAuth2 Login 성공!");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Member findMember = memberRepository.findById(oAuth2User.getMemberId())
                .orElseThrow(MemberException.MemberNotFoundException::new);

        String accessToken = jwtService.createAccessToken(findMember.getMemberId(), findMember.getRole());
        String refreshToken = jwtService.createRefreshToken();

        findMember.updateRefreshToken(refreshToken);
        findMember.updateRefreshTokenStatus(true);
        String targetUrl = createURI(accessToken, refreshToken).toString();

        response.sendRedirect(targetUrl);
    }

    private URI createURI(
            final String accessToken,
            final String refreshToken
    ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("access_token", accessToken);
        log.info("accessToken => {}", accessToken);
        queryParams.add("refresh_token", refreshToken);
        log.info("refreshToken => {}", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost:8080")
                .path("/auth/token")
                .queryParams(queryParams)
                .build()
                .encode()
                .toUri();
    }
}
