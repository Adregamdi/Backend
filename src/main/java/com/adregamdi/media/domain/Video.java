package com.adregamdi.media.domain;

import com.adregamdi.media.enumtype.MediaType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    @Comment(value = "미디어 url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    @Comment(value = "미디어 종류")
    private MediaType mediaType;

    @Column(name = "target_id")
    @Comment(value = "대상 식별 값")
    private Long targetId;

    @Column(name = "create_at", updatable = false)
    @Comment(value = "생성 날짜")
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }

    @Builder
    public Video(Long id, String url, MediaType mediaType, Long targetId) {
        this.id = id;
        this.url = url;
        this.mediaType = mediaType;
        this.targetId = targetId;
    }

    public void updateMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void updateTargetId(Long targetId) {
        this.targetId = targetId;
    }

}
