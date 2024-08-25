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

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<java.util.UUID> memberId = createComparable("memberId", java.util.UUID.class);

    public final NumberPath<Long> placeNo = createNumber("placeNo", Long.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> travelReviewNo = createNumber("travelReviewNo", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

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

