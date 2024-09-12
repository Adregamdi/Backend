package com.adregamdi.like.domain.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SelectedType {
    ALL("전체"),
    SHORTS("쇼츠"),
    PLACE("장소"),
    TRAVELOGUE("여행기");

    private final String description;
}