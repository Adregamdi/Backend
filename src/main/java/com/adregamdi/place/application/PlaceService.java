package com.adregamdi.place.application;


import com.adregamdi.place.dto.PlaceReviewDTO;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.request.CreatePlaceReviewRequest;
import com.adregamdi.place.dto.request.GetSortingPlacesRequest;
import com.adregamdi.place.dto.response.*;

import java.net.URISyntaxException;
import java.util.List;

public interface PlaceService {
    /*
     * [장소 등록]
     * */
    void create(final CreatePlaceRequest request);

    /*
     * [장소 등록 By 외부 API]
     * */
    void createByAPI();

    /*
     * [장소 리뷰 등록]
     * */
    CreatePlaceReviewResponse createReview(final CreatePlaceReviewRequest request, final String memberId);

    /*
     * [장소 추가 카운트 증감]
     * */
    void addCount(final Long placeId, final boolean choice);

    /*
     * [특정 장소 조회]
     * */
    GetPlaceResponse get(final Long placeId);

    /*
     * [장소 리스트 조회]
     * */
    GetPlacesResponse getPlaces(final int pageNo, final String name);

    /*
     * [선택 기반 추천 장소 리스트 조회]
     * */
    List<GetSelectionBasedRecommendationPlacesResponse> getSelectionBasedRecommendationPlaces(final Double latitude, final Double longitude) throws URISyntaxException;

    /*
     * [최적 거리 정렬]
     * */
    List<GetSortingPlacesResponse> getSortingPlaces(final List<GetSortingPlacesRequest> requests);

    /*
     * [일정에 많이 추가된 장소 리스트 조회]
     * */
    GetPopularPlacesResponse getPopularPlaces(final Long lastId, final Integer lastAddCount);

    /*
     * [내 리뷰 조회]
     * */
    GetMyPlaceReviewResponse getMyReview(final String memberId);

    /*
     * [특정 리뷰 조회]
     * */
    PlaceReviewDTO getReview(final Long placeReviewId);

    /*
     * [특정 장소의 전체 리뷰 조회]
     * */
    GetPlaceReviewsResponse getReviews(final Long placeId);

    /*
     * [특정 장소의 전체 사진 조회]
     * */
    GetPlaceImagesResponse getPlaceImages(final Long placeId);
}
