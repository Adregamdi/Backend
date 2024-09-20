package com.adregamdi.core.oauth2.application;

import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.dto.LoginRequest;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import com.adregamdi.core.oauth2.dto.OAuth2Attributes;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private static final String APPLE = "apple";
    private static final String KAKAO = "kakao";
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final WebClient webClient;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Map userInfo = null;
        String userInfoUrl;

        switch (request.socialType()) {
            case "kakao":
                userInfoUrl = "https://kapi.kakao.com/v2/user/me";
                userInfo = fetchUserInfo(userInfoUrl, request.oauthAccessToken());
                break;

            case "google":
                userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
                userInfo = fetchUserInfo(userInfoUrl, request.oauthAccessToken());
                break;

            case "apple":
                // 애플의 경우, 추가적인 처리 필요 (예: JWT 디코딩 등)
                // userInfoUrl = "애플 사용자 정보 URL"; // 실제 URL 설정 필요
                // userInfo = webClient.get().uri(userInfoUrl)...
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 서비스입니다.: " + request.socialType());
        }

        SocialType socialType = getSocialType(request.socialType());
        String userNameAttributeName = (request.socialType().equals("google")) ? "sub" : "id";
        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, userInfo);

        Member findMember = getMember(extractAttributes, socialType);
        findMember.updateSocialAccessToken(request.oauthAccessToken());

        String accessToken = jwtService.createAccessToken(String.valueOf(findMember.getMemberId()), findMember.getRole());
        String refreshToken = jwtService.createRefreshToken();
        findMember.updateRefreshToken(refreshToken);
        findMember.updateRefreshTokenStatus(true);

        return new LoginResponse(accessToken, refreshToken);
    }

    private Map fetchUserInfo(String userInfoUrl, String accessToken) {
        return webClient
                .get()
                .uri(userInfoUrl)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Failed to fetch user info: " + body)))
                )
                .bodyToMono(Map.class)
                .block();
    }

    private SocialType getSocialType(final String registrationId) {
        if (Objects.equals(APPLE, registrationId)) {
            return SocialType.APPLE;
        }
        if (Objects.equals(KAKAO, registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member getMember(final OAuth2Attributes attributes, final SocialType socialType) {
        Member findMember = memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
                .orElse(null);

        if (findMember == null) {
            return saveMember(attributes, socialType);
        }
        return findMember;
    }

    private Member saveMember(final OAuth2Attributes attributes, final SocialType socialType) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOauth2UserInfo());
        return memberRepository.save(createdMember);
    }
}
