package com.adregamdi.place.application;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PlaceDTO;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.place.exception.PlaceException.PlaceNotFoundException;
import com.adregamdi.place.infrastructure.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.adregamdi.core.constant.Constant.NORMAL_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageAsc;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    @Override
    @Transactional(readOnly = true)
    public GetPlaceResponse get(int pageNo, String name) {
        Slice<Place> places = placeRepository.findByNameStartingWith(generatePageAsc(pageNo, NORMAL_PAGE_SIZE, "name"), name)
                .orElseThrow(() -> new PlaceNotFoundException(name));
        return GetPlaceResponse.from(
                places.getContent()
                        .stream()
                        .map(PlaceDTO::from)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void getPlaces() {

    }
}
