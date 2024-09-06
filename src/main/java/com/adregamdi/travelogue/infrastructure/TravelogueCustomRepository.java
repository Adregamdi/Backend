package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.dto.TravelogueDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TravelogueCustomRepository {
    Slice<TravelogueDTO> findByMemberId(String memberId, Pageable pageable);
}
