package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PopularPlaceDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPopularPlacesResponse(
        int currentPage,
        int pageSize,
        boolean hasNextPlaces,
        long totalPlaces,
        List<PopularPlaceInfo> places
) {
    public static GetPopularPlacesResponse of(
            int currentPage,
            int pageSize,
            boolean hasNextPlaces,
            long totalPlaces,
            List<PopularPlaceInfo> places
    ) {
        return GetPopularPlacesResponse.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNextPlaces(hasNextPlaces)
                .totalPlaces(totalPlaces)
                .places(places)
                .build();
    }

    @Builder
    public record PopularPlaceInfo(
            Long placeId,
            String title,
            String contentsLabel,
            String regionLabel,
            List<String> imageUrls,
            Integer addCount,
            Long photoReviewCount,
            Long shortsCount
    ) {
        public static PopularPlaceInfo from(PopularPlaceDTO dto) {
            Place place = dto.place();
            return PopularPlaceInfo.builder()
                    .placeId(place.getPlaceId())
                    .title(place.getTitle())
                    .contentsLabel(place.getContentsLabel())
                    .regionLabel(place.getRegionLabel())
                    .imageUrls(dto.imageUrls())
                    .addCount(place.getAddCount())
                    .photoReviewCount(dto.photoReviewCount())
                    .shortsCount(dto.shortsCount())
                    .build();
        }
    }
}