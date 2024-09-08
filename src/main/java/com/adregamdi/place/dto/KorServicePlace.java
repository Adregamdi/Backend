package com.adregamdi.place.dto;

public record KorServicePlace(
        String addr1,
        String addr2,
        String areacode,
        String contentid,
        String contenttypeid,
        String firstimage,
        String firstimage2,
        String mapx,
        String mapy,
        String sigungucode,
        String title
) {
    public String getFullAddress() {
        return addr1 + " " + addr2;
    }

    public Double getLatitude() {
        return Double.parseDouble(mapy);
    }

    public Double getLongitude() {
        return Double.parseDouble(mapx);
    }
}
