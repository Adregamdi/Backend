package com.adregamdi.place.application;

import com.adregamdi.place.dto.response.GetPlaceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    @Override
    @Transactional(readOnly = true)
    public GetPlaceResponse get(int pageNo, String name) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public void getPlaces() {

    }
}
