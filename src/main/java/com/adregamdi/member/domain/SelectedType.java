package com.adregamdi.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SelectedType {
    ALL("전체"),
    SHORTS("쇼츠"),
    PLACE("장소"),
    TRAVELOGUE("여행기"),
    PLACE_REVIEW("장소 리뷰");

    private final String description;
}