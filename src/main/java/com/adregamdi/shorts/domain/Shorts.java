package com.adregamdi.shorts.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_shorts")
public class Shorts extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Comment(value = "쇼츠 제목")
    private String title;

    @Column
    @Comment(value = "작성자")
    private UUID memberId;

    @Column
    @Comment(value = "장소")
    private Long placeNo;

    @Column
    @Comment(value = "여행기 번호")
    private Long travelReviewNo;

    @Column
    @Comment(value = "동영상 업로드 url")
    private String shortsVideoUrl;

    @Builder
    public Shorts(Long id, String title, UUID memberId, Long placeNo, Long travelReviewNo, String shortsVideoUrl) {
        this.id = id;
        this.title = title;
        this.memberId = memberId;
        this.placeNo = placeNo;
        this.travelReviewNo = travelReviewNo;
        this.shortsVideoUrl = shortsVideoUrl;
    }

}
