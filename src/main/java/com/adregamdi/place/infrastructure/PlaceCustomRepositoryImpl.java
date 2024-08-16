package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {
    @Override
    public Optional<Slice<Place>> findByNameStartingWith(Pageable pageable, String name) {
        return Optional.empty();
    }
}
