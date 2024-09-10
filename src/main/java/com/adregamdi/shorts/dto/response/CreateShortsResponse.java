package com.adregamdi.shorts.dto.response;

import com.adregamdi.shorts.domain.Shorts;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateShortsResponse {

    private Long shortsId;
    private String title;
    private UUID memberId;
    private Long placeId;
    private Long travelogueId;

    public static CreateShortsResponse of(Shorts shorts) {
        return new CreateShortsResponse(
                shorts.getShortsId(),
                shorts.getTitle(),
                shorts.getMemberId(),
                shorts.getPlaceId(),
                shorts.getTravelogueId()
        );
    }

}
