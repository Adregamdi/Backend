package com.adregamdi.place.dto;

import com.adregamdi.place.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private Long placeId;
    private String title;
    private String contentsLabel;
    private String regionLabel;
    private String region1Cd;
    private String region2Cd;
    private String address;
    private String roadAddress;
    private String tag;
    private String introduction;
    private String information;
    private Double latitude;
    private Double longitude;
    private String phoneNo;
    private String imgPath;
    private String thumbnailPath;

    public static PlaceDTO from(Place place) {
        return PlaceDTO.builder()
                .placeId(place.getPlaceId())
                .title(place.getTitle())
                .contentsLabel(place.getContentsLabel())
                .regionLabel(place.getRegionLabel())
                .region1Cd(place.getRegion1Cd())
                .region2Cd(place.getRegion2Cd())
                .address(place.getAddress())
                .roadAddress(place.getRoadAddress())
                .tag(place.getTag())
                .introduction(place.getIntroduction())
                .information(place.getInformation())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .phoneNo(place.getPhoneNo())
                .imgPath(place.getImgPath())
                .thumbnailPath(place.getThumbnailPath())
                .build();
    }
}
