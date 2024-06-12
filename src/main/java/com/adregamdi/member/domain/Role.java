package com.adregamdi.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("GUEST"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    private final String key;
}
