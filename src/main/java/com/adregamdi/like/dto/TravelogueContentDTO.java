package com.adregamdi.like.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TravelogueContentDTO {

    private Long travelogueId;
    private String title;
    private List<String> imageList;
    private String handle;
    private String profile;

    public TravelogueContentDTO(Long travelogueId, String title, List<String> imageList, String handle, String profile) {
        this.travelogueId = travelogueId;
        this.title = title;
        this.imageList = imageList;
        this.handle = handle;
        this.profile = profile;
    }

}