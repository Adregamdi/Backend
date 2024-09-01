package com.adregamdi.travelogue.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travelogue_image")
public class TravelogueImage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelogueImageId;
    @Column
    private Long travelogueId; // 여행기 id
    @Column
    private String url; // 이미지 url

    public TravelogueImage(Long travelogueId, String url) {
        this.travelogueId = travelogueId;
        this.url = url;
    }
}
