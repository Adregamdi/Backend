package com.adregamdi.member.dto.response;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.domain.SocialType;
import lombok.Builder;

@Builder
public record GetMyMemberResponse(
        String name,
        String profile,
        String handle,
        String email,
        String age,
        String gender,
        SocialType socialType,
        Role role
) {
    public static GetMyMemberResponse from(Member member) {
        return GetMyMemberResponse.builder()
                .name(member.getName())
                .profile(member.getProfile())
                .handle(member.getHandle())
                .email(member.getEmail())
                .age(member.getAge())
                .gender(member.getGender())
                .socialType(member.getSocialType())
                .role(member.getRole())
                .build();
    }
}
