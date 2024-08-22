package com.adregamdi.shorts.dto;

import com.adregamdi.shorts.domain.Shorts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsDTO {

    private Long shortsId;
    private String title;
    private UUID memberId;
    private Long placeNo;
    private Long travelReviewNo;
    private String shortsVideoUrl;

    public static ShortsDTO of(Shorts shorts) {
        return new ShortsDTO(
                shorts.getId(),
                shorts.getTitle(),
                shorts.getMemberId(),
                shorts.getPlaceNo(),
                shorts.getTravelReviewNo(),
                shorts.getShortsVideoUrl()
        );
    }
}
