package com.adregamdi.shorts.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QShorts is a Querydsl query type for Shorts
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShorts extends EntityPathBase<Shorts> {

    private static final long serialVersionUID = -1484669053L;

    public static final QShorts shorts = new QShorts("shorts");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final BooleanPath assignedStatus = createBoolean("assignedStatus");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ComparablePath<java.util.UUID> memberId = createComparable("memberId", java.util.UUID.class);

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Long> shortsId = createNumber("shortsId", Long.class);

    public final StringPath shortsVideoUrl = createString("shortsVideoUrl");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final NumberPath<Long> travelogueId = createNumber("travelogueId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QShorts(String variable) {
        super(Shorts.class, forVariable(variable));
    }

    public QShorts(Path<? extends Shorts> path) {
        super(path.getType(), path.getMetadata());
    }

    public QShorts(PathMetadata metadata) {
        super(Shorts.class, metadata);
    }

}

