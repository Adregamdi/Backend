package com.adregamdi.block.dto.response;

import com.adregamdi.block.domain.Block;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyBlockingMembers(
        List<Block> blocks
) {
    public static GetMyBlockingMembers from(final List<Block> blocks) {
        return GetMyBlockingMembers.builder()
                .blocks(blocks)
                .build();
    }
}
