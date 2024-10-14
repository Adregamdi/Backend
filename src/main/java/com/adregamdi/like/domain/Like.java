package com.adregamdi.like.domain;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.core.entity.BaseTime;
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
@Table(name = "tbl_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "content_type", "content_id"}),
        indexes = {
                @Index(name = "idx_member_content", columnList = "memberId, contentType, contentId"),
                @Index(name = "idx_content", columnList = "contentType, contentId")
        })
public class Like extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Comment("회원 id")
    private String memberId;

    @Column(name = "content_type", nullable = false)
    @Comment("컨텐츠 타입")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "content_id", updatable = false, nullable = false)
    @Comment("컨텐츠 id")
    private Long contentId;

    @Builder
    public Like(String memberId, ContentType contentType, Long contentId) {
        this.memberId = memberId;
        this.contentType = contentType;
        this.contentId = contentId;
    }

}