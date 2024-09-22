package com.adregamdi.travelogue.dto.response;

import com.adregamdi.travelogue.dto.HotTravelogueDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetHotTraveloguesResponse(
        boolean hasNext,
        List<HotTravelogueDTO> travelogues
) {

}
