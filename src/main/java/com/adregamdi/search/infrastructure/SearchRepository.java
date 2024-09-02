package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.SearchItemDTO;
import com.adregamdi.search.dto.SearchType;

import java.util.List;
import java.util.Set;

public interface SearchRepository {
    List<SearchItemDTO> search(final String keyword, final int page, final int pageSize, final Set<SearchType> types);

    long countTotal(final String keyword);
}
