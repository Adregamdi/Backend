package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.dto.TravelogueDTO;
import com.adregamdi.travelogue.dto.response.GetHotTraveloguesResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TravelogueCustomRepository {
    Slice<TravelogueDTO> findByMemberId(String memberId, Pageable pageable);

    Slice<TravelogueDTO> findOrderByCreatedAt(String memberId, Pageable pageable);

    GetHotTraveloguesResponse findOrderByLikeCount(String memberId, int likeCount, int size);
}
