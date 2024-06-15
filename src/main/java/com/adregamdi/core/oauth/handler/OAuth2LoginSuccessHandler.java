package com.adregamdi.core.oauth.handler;

import com.adregamdi.core.oauth.domain.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        log.info("OAuth2 Login 성공!");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 인가 코드 및 회원 권한 URL에 담기
        String targetUrl = createURI(code, String.valueOf(oAuth2User.getRole())).toString();

        // 프론트의 토큰 관리 페이지로 리다이렉트
        response.sendRedirect(targetUrl);
    }

    // 자체 인가 코드를 URL에 담아 리다이렉트
    private URI createURI(
            final String code,
            final String role
    ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("code", code);
        queryParams.add("role", role);
        log.info("인가 코드 담기 성공");

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("15.165.1.48")
//               .scheme("https")
//               .host("coverflow.co.kr")
                .path("/auth/token")
                .queryParams(queryParams)
                .build()
                .encode()
                .toUri();
    }
}
