package com.adregamdi.shorts.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "title")
    @Comment(value = "쇼츠 제목")
    private String title;

    @Column(name = "member_id")
    @Comment(value = "작성자")
    private UUID memberId;

    @Column(name = "place_no")
    @Comment(value = "장소")
    private Long placeNo;

    @Column(name = "travel_review_no")
    @Comment(value = "여행기 번호")
    private Long travelReviewNo;

    @Column(name = "shorts_video_url", nullable = false, unique = true)
    @Comment(value = "동영상 업로드 url")
    private String shortsVideoUrl;

    @Column(name = "thumbnail_url", nullable = false, unique = true)
    @Comment(value = "동영상 썸네일 url")
    private String thumbnailUrl;

    @Column(name = "assigned_status")
    @Comment(value = "할당 여부")
    private boolean assignedStatus;

//    @Column(name = "is_deleted")
//    @Comment(value = "삭제 여부")
//    private boolean isDeleted;

    @Column(name = "view_count")
    @Comment(value = "조회 수")
    private long viewCount;

    @Builder
    public Shorts(Long id, String title, UUID memberId, Long placeNo, Long travelReviewNo, String shortsVideoUrl, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.memberId = memberId;
        this.placeNo = placeNo;
        this.travelReviewNo = travelReviewNo;
        this.shortsVideoUrl = shortsVideoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.assignedStatus = false;
//        this.isDeleted = false;
        this.viewCount = 0L;
    }

    public void update(UpdateShortsRequest request) {
        this.title = request.title();
        this.placeNo = request.placeNo();
        this.travelReviewNo = request.travelReviewNo();
        this.shortsVideoUrl = request.videoUrl();
        this.thumbnailUrl = request.thumbnailUrl();
    }

    public void assign(CreateShortsRequest request) {
        this.title = request.title();
        this.placeNo = request.placeNo();
        this.travelReviewNo = request.travelReviewNo();
        this.assignedStatus = true;
    }

//    public void delete() {
//        this.isDeleted = true;
//    }
}
