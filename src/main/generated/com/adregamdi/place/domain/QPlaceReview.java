package com.adregamdi.place.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlaceReview is a Querydsl query type for PlaceReview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaceReview extends EntityPathBase<PlaceReview> {

    private static final long serialVersionUID = 795339381L;

    public static final QPlaceReview placeReview = new QPlaceReview("placeReview");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath memberId = createString("memberId");

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Long> placeReviewId = createNumber("placeReviewId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QPlaceReview(String variable) {
        super(PlaceReview.class, forVariable(variable));
    }

    public QPlaceReview(Path<? extends PlaceReview> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlaceReview(PathMetadata metadata) {
        super(PlaceReview.class, metadata);
    }

}

