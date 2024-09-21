package com.adregamdi.media.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_image")
public class Image extends BaseTime {

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

    @Builder
    public Image(Long imageNo, String imageUrl, ImageTarget imageTarget, String targetId) {
        this.imageNo = imageNo;
        this.imageUrl = imageUrl;
        this.imageTarget = imageTarget;
        this.targetId = targetId;
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
