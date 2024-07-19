package com.adregamdi.member.dto.response;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.domain.SocialType;
import lombok.Builder;

@Builder
public record GetMyMemberResponse(
        String nickname,
        String email,
        String age,
        String gender,
        SocialType socialType,
        Role role
) {
    public static GetMyMemberResponse from(Member member) {
        return GetMyMemberResponse.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .gender(member.getGender())
                .socialType(member.getSocialType())
                .role(member.getRole())
                .build();
    }
}
