package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.TravelDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelDayRepository extends JpaRepository<TravelDay, Long> {
    Optional<List<TravelDay>> findByTravelId(final Long travelId);
}
