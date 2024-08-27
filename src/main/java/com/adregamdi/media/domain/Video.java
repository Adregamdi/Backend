package com.adregamdi.media.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.media.enumtype.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Video extends BaseTime {

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

    @Builder
    public Video(String url, MediaType mediaType, Long targetId) {
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

    public void updateUrl(String url) {
        this.url = url;
    }

}
