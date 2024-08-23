package com.adregamdi.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtil {

    public static Pageable generatePageAsc(
            final int pageNo,
            final int pageSize,
            final String... criterion
    ) {
        return PageRequest.of(pageNo, pageSize, Sort.by(criterion).ascending());
    }

    public static Pageable generatePageDesc(
            final int pageNo,
            final int pageSize,
            final String criterion
    ) {
        return PageRequest.of(pageNo, pageSize, Sort.by(criterion).descending());
    }
}

