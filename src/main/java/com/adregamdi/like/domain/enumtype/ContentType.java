package com.adregamdi.like.domain.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    SHORTS("쇼츠"),
    PLACE("장소"),
    TRAVELOGUE("여행기");

    private final String description;

    public static boolean isValid(String value) {
        for (ContentType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}