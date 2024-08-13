package com.adregamdi.core.oauth2.service;

import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.core.oauth2.dto.LoginRequest;
import com.adregamdi.core.oauth2.dto.LoginResponse;
import com.adregamdi.core.oauth2.dto.OAuth2Attributes;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        Map userInfo = webClient
                .get()
                .uri(userInfoUrl)
                .headers(headers -> headers.setBearerAuth(request.oauthAccessToken()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        SocialType socialType = getSocialType("kakao");
        String userNameAttributeName = "id";
        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, userInfo);

        Member findMember = getMember(extractAttributes, socialType);
        findMember.updateSocialAccessToken(request.oauthAccessToken());

        String accessToken = jwtService.createAccessToken(String.valueOf(findMember.getId()), findMember.getRole());
        String refreshToken = jwtService.createRefreshToken();
        findMember.updateRefreshToken(refreshToken);
        findMember.updateRefreshTokenStatus(true);

        return new LoginResponse(accessToken, refreshToken);
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
