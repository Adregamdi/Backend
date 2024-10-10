package com.adregamdi.block.application;

import com.adregamdi.block.dto.response.CreateBlockResponse;
import com.adregamdi.block.dto.response.GetMyBlockingMembers;

public interface BlockService {
    CreateBlockResponse create(final String memberId, final String blockedMemberId);

    GetMyBlockingMembers getMyBlockingMembers(final String memberId);

    void delete(final String memberId, final String blockedMemberId);
}
