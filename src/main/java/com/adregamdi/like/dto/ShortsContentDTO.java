package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsContentDTO {

    private Long shortsId;
    private String title;
    private String shortsVideoUrl;
    private String thumbnailUrl;
}