package com.adregamdi.core.oauth2.application;

import java.util.Map;

public abstract class OAuth2UserInfo {

    final protected Map<String, Object> attributes;

    public OAuth2UserInfo(
            final Map<String, Object> attributes
    ) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"

    public abstract String getEmail();

    public abstract String getAge();

    public abstract String getGender();

    public abstract String getName();
}
