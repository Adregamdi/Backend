package com.adregamdi.shorts.domain;

import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_shorts")
public class Shorts {

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

//    @Column
//    @Comment(value = "동영상 업로드 url")
//    private String shortsVideoUrl;

    @Column
    @Comment(value = "조회 수")
    private long viewCount;

    @CreatedDate
    @Column(name = "create_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Shorts(Long id, String title, UUID memberId, Long placeNo, Long travelReviewNo, long viewCount) {
        this.id = id;
        this.title = title;
        this.memberId = memberId;
        this.placeNo = placeNo;
        this.travelReviewNo = travelReviewNo;
        this.viewCount = viewCount;
    }

    public void update(UpdateShortsRequest request) {
        this.title = request.title();
        this.placeNo = request.placeNo();
        this.travelReviewNo = request.travelReviewNo();
    }
}
