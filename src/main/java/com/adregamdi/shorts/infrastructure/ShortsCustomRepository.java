package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.dto.response.GetShortsResponse;

public interface ShortsCustomRepository {

    GetShortsResponse getShortsForMember(String memberId, long lastId, int size);
}
