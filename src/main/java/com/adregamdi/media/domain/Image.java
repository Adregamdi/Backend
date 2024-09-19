package com.adregamdi.media.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_no")
    private Long imageNo;

    @Column(name = "image_url", nullable = false)
    @Comment(value = "이미지 URL")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_target")
    @Comment(value = "관련 엔티티")
    private ImageTarget imageTarget;

    @Column(name = "target_id")
    @Comment(value = "관련 엔티티 식별 값")
    private String targetId;

    @Column(name = "create_date", updatable = false)
    @Comment(value = "이미지 생성일")
    private LocalDateTime createDate;


    @Builder
    public Image(Long imageNo, String imageUrl, ImageTarget imageTarget, String targetId) {
        this.imageNo = imageNo;
        this.imageUrl = imageUrl;
        this.imageTarget = imageTarget;
        this.targetId = targetId;
        this.createDate = LocalDateTime.now();
    }

    public void updateTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void updateImageTarget(ImageTarget imageTarget) {
        this.imageTarget = imageTarget;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
