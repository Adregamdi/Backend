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
    void create(CreatePlaceRequest request);

    /*
     * [장소 등록 By 외부 API]
     * */
    void createByAPI();

    /*
     * [장소 리뷰 등록]
     * */
    CreatePlaceReviewResponse createReview(String memberId, CreatePlaceReviewRequest request);

    /*
     * [장소 추가 카운트 증감]
     * */
    void addCount(Long placeId, boolean choice);

    /*
     * [특정 장소 조회]
     * */
    GetPlaceResponse get(String memberId, Long placeId);

    /*
     * [장소 리스트 조회]
     * */
    GetPlacesResponse getPlaces(int pageNo, String name);

    /*
     * [선택 기반 추천 장소 리스트 조회]
     * */
    List<GetSelectionBasedRecommendationPlacesResponse> getSelectionBasedRecommendationPlaces(Double latitude, Double longitude) throws URISyntaxException;

    /*
     * [최적 거리 정렬]
     * */
    List<GetSortingPlacesResponse> getSortingPlaces(List<GetSortingPlacesRequest> requests);

    /*
     * [일정에 많이 추가된 장소 리스트 조회]
     * */
    GetPopularPlacesResponse getPopularPlaces(Long lastId, Integer lastAddCount);

    /*
     * [내 리뷰 조회]
     * */
    GetMyPlaceReviewResponse getMyReview(String memberId);

    /*
     * [특정 리뷰 조회]
     * */
    PlaceReviewDTO getReview(String memberId, Long placeReviewId);

    /*
     * [특정 장소의 전체 리뷰 조회]
     * */
    GetPlaceReviewsResponse getReviews(String memberId, Long placeId);

    /*
     * [특정 장소의 전체 사진 조회]
     * */
    GetPlaceImagesResponse getPlaceImages(Long placeId);

    /*
     * [특정 회원의 모든 리뷰 삭제]
     * */
    void deleteMyReview(String memberId);
}
