package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.dto.response.GetShortsResponse;

import java.util.UUID;

public interface ShortsCustomRepository {

    GetShortsResponse getShortsForMember(UUID memberId, long lastId, int size);

    GetShortsResponse getUserShorts(UUID memberId, long lastShortsId, int size);
}
