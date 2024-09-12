package com.adregamdi.like.domain;

import com.adregamdi.like.domain.enumtype.ContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_like", indexes = {
        @Index(name = "idx_user_content", columnList = "userId, contentType, contentId"),
        @Index(name = "idx_content", columnList = "contentType, contentId")
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column(name = "user_id", updatable = false, nullable = false)
    @Comment("회원 id")
    private UUID memberId;

    @Column(name = "content_type", nullable = false)
    @Comment("컨텐츠 타입")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "content_id", updatable = false, nullable = false)
    @Comment("컨텐츠 id")
    private Long contentId;

    @Column(name = "create_at", updatable = false)
    @Comment("생성 시간")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Builder
    public Like(UUID memberId, ContentType contentType, Long contentId, LocalDateTime createAt) {
        this.memberId = memberId;
        this.contentType = contentType;
        this.contentId = contentId;
        this.createAt = createAt;
    }

}