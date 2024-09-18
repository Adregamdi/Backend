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
    private String name; // 작성자 닉네임
    private String profile; // 작성자 프로필 사진

    public TravelogueContentDTO(Long travelogueId, String title, List<String> imageList, String name, String profile) {
        this.travelogueId = travelogueId;
        this.title = title;
        this.imageList = imageList;
        this.name = name;
        this.profile = profile;
    }

}