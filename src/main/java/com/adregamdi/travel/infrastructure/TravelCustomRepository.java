package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.dto.TravelDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface TravelCustomRepository {
    Slice<TravelDTO> findByMemberId(String memberId, Pageable pageable);
}
