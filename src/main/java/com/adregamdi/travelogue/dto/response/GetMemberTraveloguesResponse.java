package com.adregamdi.travelogue.dto.response;

import com.adregamdi.travelogue.dto.TravelogueDTO;

import java.util.List;

public record GetMemberTraveloguesResponse(
        boolean hasNext,
        List<TravelogueDTO> travelogueList
) {
}
