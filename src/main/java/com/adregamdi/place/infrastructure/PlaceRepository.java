package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {
}
