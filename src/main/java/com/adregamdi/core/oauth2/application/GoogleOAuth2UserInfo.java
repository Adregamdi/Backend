package com.adregamdi.core.oauth2.application;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(
            final Map<String, Object> attributes
    ) {
        super(attributes);
        log.info("attributes: {}", attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAge() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
