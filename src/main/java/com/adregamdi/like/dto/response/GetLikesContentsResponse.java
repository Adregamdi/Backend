package com.adregamdi.like.dto.response;

import com.adregamdi.like.domain.enumtype.SelectedType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetLikesContentsResponse<T> {

    private SelectedType selectedType;
    private boolean hasNext;
    private T contents;

}