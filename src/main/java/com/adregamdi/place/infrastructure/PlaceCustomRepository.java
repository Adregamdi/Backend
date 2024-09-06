package com.adregamdi.place.infrastructure;


import com.adregamdi.place.domain.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface PlaceCustomRepository {
    Optional<Slice<Place>> findByTitleStartingWith(String title, Pageable pageable);
}
