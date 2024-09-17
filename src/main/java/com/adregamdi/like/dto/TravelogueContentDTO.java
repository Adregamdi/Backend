package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelogueContentDTO {

    private Long travelogueId;
    private String title;
    private List<String> imageList;
    private String name; // 작성자 닉네임
    private String profile; // 작성자 프로필 사진

}