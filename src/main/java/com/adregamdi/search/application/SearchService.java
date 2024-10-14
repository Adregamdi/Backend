package com.adregamdi.search.application;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.search.dto.response.SearchResponse;

import java.util.Set;

public interface SearchService {
    /*
     * [콘텐츠 검색]
     * */
    SearchResponse search(String keyword, int page, Set<ContentType> types, String memberId);
}
