package com.adregamdi.member.dto.request;


import java.time.LocalDateTime;

public record GetMemberContentsRequest(

        String memberId,
        LocalDateTime createAt,
        int size
) {
}
