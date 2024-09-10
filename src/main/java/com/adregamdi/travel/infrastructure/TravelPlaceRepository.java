package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long>, TravelCustomRepository {
    List<TravelPlace> findByTravelDayId(Long travelDayId);

    List<TravelPlace> findAllByTravelDayId(Long travelDayId);

    void deleteAllByTravelDayId(Long travelDayId);
}
