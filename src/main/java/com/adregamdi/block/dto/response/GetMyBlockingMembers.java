package com.adregamdi.block.dto.response;

import com.adregamdi.block.dto.BlockDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyBlockingMembers(
        List<BlockDTO> blocks
) {
    public static GetMyBlockingMembers from(final List<BlockDTO> blocks) {
        return GetMyBlockingMembers.builder()
                .blocks(blocks)
                .build();
    }
}
