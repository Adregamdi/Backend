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
    private Long shortsId;

    @Column(name = "title")
    @Comment(value = "쇼츠 제목")
    private String title;

    @Column(name = "member_id")
    @Comment(value = "작성자")
    private UUID memberId;

    @Column(name = "place_id")
    @Comment(value = "장소 id")
    private Long placeId;

    @Column(name = "travelogue_id")
    @Comment(value = "여행기 id")
    private Long travelogueId;

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
    private int viewCount;

    @Builder
    public Shorts(Long shortsId, String title, UUID memberId, Long placeId, Long travelogueId, String shortsVideoUrl, String thumbnailUrl) {
        this.shortsId = shortsId;
        this.title = title;
        this.memberId = memberId;
        this.placeId = placeId;
        this.travelogueId = travelogueId;
        this.shortsVideoUrl = shortsVideoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.assignedStatus = false;
//        this.isDeleted = false;
        this.viewCount = 0;
    }

    public void update(UpdateShortsRequest request) {
        this.title = request.title();
        this.placeId = request.placeId();
        this.travelogueId = request.travelogueId();
        this.shortsVideoUrl = request.videoUrl();
        this.thumbnailUrl = request.thumbnailUrl();
    }

    public void assign(CreateShortsRequest request) {
        this.title = request.title();
        this.placeId = request.placeId();
        this.travelogueId = request.travelogueId();
        this.assignedStatus = true;
    }

//    public void delete() {
//        this.isDeleted = true;
//    }
}
