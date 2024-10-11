package com.adregamdi.block.application;

import com.adregamdi.block.dto.response.CreateBlockResponse;
import com.adregamdi.block.dto.response.GetMyBlockingMembers;

public interface BlockService {
    /*
     * [차단하기]
     * */
    CreateBlockResponse create(final String memberId, final String blockedMemberId);

    /*
     * [내 차단 목록 조회]
     * */
    GetMyBlockingMembers getMyBlockingMembers(final String memberId);

    /*
     * [차단해제]
     * */
    void delete(final String memberId, final String blockedMemberId);
}
