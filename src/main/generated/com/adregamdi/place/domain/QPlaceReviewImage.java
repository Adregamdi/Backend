package com.adregamdi.place.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlaceReviewImage is a Querydsl query type for PlaceReviewImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaceReviewImage extends EntityPathBase<PlaceReviewImage> {

    private static final long serialVersionUID = 1926828006L;

    public static final QPlaceReviewImage placeReviewImage = new QPlaceReviewImage("placeReviewImage");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> placeReviewId = createNumber("placeReviewId", Long.class);

    public final NumberPath<Long> placeReviewImageId = createNumber("placeReviewImageId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath url = createString("url");

    public QPlaceReviewImage(String variable) {
        super(PlaceReviewImage.class, forVariable(variable));
    }

    public QPlaceReviewImage(Path<? extends PlaceReviewImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlaceReviewImage(PathMetadata metadata) {
        super(PlaceReviewImage.class, metadata);
    }

}

