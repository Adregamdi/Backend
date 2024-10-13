package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.domain.TravelDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelDayRepository extends JpaRepository<TravelDay, Long> {
    List<TravelDay> findByTravelId(Long travelId);

    List<TravelDay> findAllByTravelId(Long travelId);
}
