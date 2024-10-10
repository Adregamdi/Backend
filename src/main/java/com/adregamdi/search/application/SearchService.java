package com.adregamdi.search.application;

import com.adregamdi.search.dto.SearchType;
import com.adregamdi.search.dto.response.SearchResponse;

import java.util.Set;

public interface SearchService {
    SearchResponse search(final String keyword, final int page, final Set<SearchType> types, final String memberId);
}
