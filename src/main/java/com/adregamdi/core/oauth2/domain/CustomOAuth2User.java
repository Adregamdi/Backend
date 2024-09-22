package com.adregamdi.core.oauth2.domain;

import com.adregamdi.member.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String memberId;
    private final Role role;

    public CustomOAuth2User(
            final Collection<? extends GrantedAuthority> authorities,
            final Map<String, Object> attributes,
            final String nameAttributeKey,
            final String memberId,
            final Role role
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.memberId = memberId;
        this.role = role;
    }
}
