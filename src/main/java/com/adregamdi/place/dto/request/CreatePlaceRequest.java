package com.adregamdi.place.dto.request;

public record CreatePlaceRequest(
        String title,
        String contentsLabel,
        String regionLabel,
        String region1Cd,
        String region2Cd,
        String address,
        String roadAddress,
        String tag,
        String introduction,
        String information,
        Double latitude,
        Double longitude,
        String phoneNo,
        String imgPath,
        String thumbnailPath
) {
}
