package com.adregamdi.travel.dto.request;

import jakarta.validation.constraints.Positive;

public record GetMyTravelRequest(
        @Positive
        Long travelId
) {
}
