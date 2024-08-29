package com.adregamdi.member.dto.request;

public record UpdateMyMemberRequest(
        String name,
        String profile,
        String handle
) {
}
