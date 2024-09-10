package com.adregamdi.like.domain.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    SHORTS("쇼츠"),
    PLACE("장소"),
    TRAVEL("여행기");

    private final String description;
}