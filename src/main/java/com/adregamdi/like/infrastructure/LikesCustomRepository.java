package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.PlaceContentDTO;
import com.adregamdi.like.dto.ShortsContentDTO;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;

import java.util.List;

public interface LikesCustomRepository {

    GetLikesContentsResponse<List<AllContentDTO>> getLikesContentsOfAll(GetLikesContentsRequest request);

    GetLikesContentsResponse<List<ShortsContentDTO>> getLikesContentsOfShorts(GetLikesContentsRequest request);

    GetLikesContentsResponse<List<PlaceContentDTO>> getLikesContentsOfPlace(GetLikesContentsRequest request);

    GetLikesContentsResponse<?> getLikesContentsOfTravelogue(GetLikesContentsRequest request);

    Boolean checkIsLiked(String memberId, ContentType contentType, Long contentId);
}