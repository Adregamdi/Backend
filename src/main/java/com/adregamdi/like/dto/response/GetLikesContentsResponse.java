package com.adregamdi.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetLikesContentsResponse<T> {

    private boolean hasNext;
    private T contents;

}