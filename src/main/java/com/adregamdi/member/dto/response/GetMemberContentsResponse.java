package com.adregamdi.member.dto.response;

public record GetMemberContentsResponse<T> (
        boolean hasNext,
        T contents
) {
}
