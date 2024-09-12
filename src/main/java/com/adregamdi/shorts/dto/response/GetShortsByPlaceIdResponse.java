package com.adregamdi.shorts.dto.response;

import com.adregamdi.shorts.dto.ShortsDTO;

import java.util.List;

public record GetShortsByPlaceIdResponse(
        List<ShortsDTO> shortsList,
        Boolean hasNext
) {
}
