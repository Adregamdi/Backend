package com.adregamdi.search.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.search.application.SearchService;
import com.adregamdi.search.dto.SearchType;
import com.adregamdi.search.dto.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/api/search")
@RestController
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @RequestParam(required = false) final String keyword,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(required = false) final Set<SearchType> types
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<SearchResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(searchService.search(keyword, page, types != null ? types : EnumSet.allOf(SearchType.class)))
                        .build()
                );
    }
}
