package com.adregamdi.member.dto.response;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.domain.SocialType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetMyMemberResponse(
        String memberId,
        String name,
        String profile,
        String handle,
        String email,
        String age,
        String gender,
        SocialType socialType,
        LocalDateTime connectedAt,
        Role role
) {
    public static GetMyMemberResponse from(Member member) {
        return GetMyMemberResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .profile(member.getProfile())
                .handle(member.getHandle())
                .email(member.getEmail())
                .age(member.getAge())
                .gender(member.getGender())
                .socialType(member.getSocialType())
                .connectedAt(member.getConnectedAt())
                .role(member.getRole())
                .build();
    }
}
