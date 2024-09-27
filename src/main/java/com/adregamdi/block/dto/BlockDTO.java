package com.adregamdi.block.dto;

import lombok.Builder;

@Builder
public record BlockDTO(
        Long blockId,
        String blockedMemberId,
        String blockedMemberName,
        String blockedMemberProfile,
        String blockedMemberHandle
) {
}
