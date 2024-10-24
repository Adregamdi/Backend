package com.adregamdi.core.oauth2.dto;

import com.adregamdi.core.oauth2.application.AppleOAuth2UserInfo;
import com.adregamdi.core.oauth2.application.GoogleOAuth2UserInfo;
import com.adregamdi.core.oauth2.application.KakaoOAuth2UserInfo;
import com.adregamdi.core.oauth2.application.OAuth2UserInfo;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuth2Attributes {

    private final String nameAttributeKey;
    private final OAuth2UserInfo oauth2UserInfo;

    @Builder
    private OAuth2Attributes(
            final String nameAttributeKey,
            final OAuth2UserInfo oauth2UserInfo
    ) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuth2Attributes of(
            final SocialType socialType,
            final String userNameAttributeName,
            final Map<String, Object> attributes
    ) {
        if (socialType == SocialType.APPLE) {
            return ofApple(userNameAttributeName, attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofApple(
            final String userNameAttributeName,
            final Map<String, Object> attributes
    ) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new AppleOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuth2Attributes ofKakao(
            final String userNameAttributeName,
            final Map<String, Object> attributes
    ) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuth2Attributes ofGoogle(
            final String userNameAttributeName,
            final Map<String, Object> attributes
    ) {
        return OAuth2Attributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(
            final SocialType socialType,
            final OAuth2UserInfo oauth2UserInfo
    ) {
        String uuid = String.valueOf(UUID.randomUUID());
        String name = "미지정";
        String profile = "https://adregamdi-dev2.s3.ap-northeast-2.amazonaws.com/profile/default_profile_image.png";
        String handle = uuid + "-ad";
        String email = uuid + "@adregamdi.com";
        String age = "Unknown";
        String gender = "Unknown";

        if (oauth2UserInfo.getEmail() != null) {
            int atIndex = oauth2UserInfo.getEmail().indexOf("@");
            email = oauth2UserInfo.getEmail();

            if (socialType == SocialType.APPLE) {
                handle = oauth2UserInfo.getEmail().substring(0, atIndex) + "-ap";
            }
            if (socialType == SocialType.GOOGLE) {
                handle = oauth2UserInfo.getEmail().substring(0, atIndex) + "-go";
            }
            if (socialType == SocialType.KAKAO) {
                handle = oauth2UserInfo.getEmail().substring(0, atIndex) + "-ka";
            }
        }
        if (oauth2UserInfo.getAge() != null) {
            age = oauth2UserInfo.getAge();
        }
        if (oauth2UserInfo.getGender() != null) {
            gender = oauth2UserInfo.getGender();
        }
        if (oauth2UserInfo.getName() != null) {
            name = oauth2UserInfo.getName();
        }

        return Member.builder()
                .name(name)
                .profile(profile)
                .handle(handle)
                .email(email)
                .age(age)
                .gender(gender)
                .socialId(oauth2UserInfo.getId())
                .socialType(socialType)
                .build();
    }
}
