package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {
    Optional<Place> findByTitleAndContentsLabel(String title, String ContentsLabel);

    Optional<Place> findByTitle(String title);
}
