package com.adregamdi.place.infrastructure;


import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PopularPlaceDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PlaceCustomRepository {
    Optional<Slice<Place>> findByTitleStartingWith(final String title, final Pageable pageable);

    List<PopularPlaceDTO> findInOrderOfPopularAddCount(final Long lastId, final Integer lastAddCount);
}
