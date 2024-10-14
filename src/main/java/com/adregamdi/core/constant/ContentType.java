package com.adregamdi.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    ALL("전체"),
    TRAVELOGUE("여행기"),
    SHORTS("쇼츠"),
    PLACE("장소");

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
