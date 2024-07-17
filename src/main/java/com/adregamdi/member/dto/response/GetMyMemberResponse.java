package com.adregamdi.member.dto.response;

import com.adregamdi.member.domain.Member;
import lombok.Builder;

@Builder
public record GetMyMemberResponse(

) {
    public static GetMyMemberResponse from(Member member) {
        return GetMyMemberResponse.builder()
                .build();
    }
}
