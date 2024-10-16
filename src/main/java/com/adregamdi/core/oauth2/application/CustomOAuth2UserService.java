package com.adregamdi.core.oauth2.application;


import com.adregamdi.core.oauth2.domain.CustomOAuth2User;
import com.adregamdi.core.oauth2.dto.OAuth2Attributes;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String APPLE = "apple";
    private static final String KAKAO = "kakao";
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
        log.info("{} socialAccessToken", userRequest.getAccessToken().getTokenValue());

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        SocialType socialType = getSocialType(userRequest.getClientRegistration().getRegistrationId());
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, attributes);
        Member createdMember = getMember(extractAttributes, socialType);

        createdMember.updateSocialAccessToken(userRequest.getAccessToken().getTokenValue());

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdMember.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdMember.getMemberId(),
                createdMember.getRole()
        );
    }

    private SocialType getSocialType(final String registrationId) {
        if (Objects.equals("apple", registrationId)) {
            return SocialType.APPLE;
        }
        if (Objects.equals("kakao", registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member getMember(
            final OAuth2Attributes attributes,
            final SocialType socialType
    ) {
        Member findMember = memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
                .orElse(null);

        // 회원 데이터 없으면 데이터 생성
        if (findMember == null) {
            return saveMember(attributes, socialType);
        }

        // 기존 회원이면 그대로 리턴
        return findMember;
    }

    private Member saveMember(
            final OAuth2Attributes attributes,
            final SocialType socialType
    ) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOauth2UserInfo());
        return memberRepository.save(createdMember);
    }
}
