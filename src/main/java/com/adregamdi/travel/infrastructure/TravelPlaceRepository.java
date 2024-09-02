package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long>, TravelCustomRepository {
    Optional<List<TravelPlace>> findByTravelDayId(Long travelDayId);

    TravelPlace findByTravelDayIdAndPlaceOrder(Long travelDayId, Integer order);
}
