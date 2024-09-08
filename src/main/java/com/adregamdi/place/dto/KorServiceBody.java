package com.adregamdi.place.dto;

public record KorServiceBody(
        KorServiceItems items,
        int numOfRows,
        int pageNo,
        int totalCount
) {
}
