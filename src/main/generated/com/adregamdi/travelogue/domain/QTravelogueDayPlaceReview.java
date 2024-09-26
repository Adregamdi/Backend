package com.adregamdi.travelogue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelogueDayPlaceReview is a Querydsl query type for TravelogueDayPlaceReview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelogueDayPlaceReview extends EntityPathBase<TravelogueDayPlaceReview> {

    private static final long serialVersionUID = 583607484L;

    public static final QTravelogueDayPlaceReview travelogueDayPlaceReview = new QTravelogueDayPlaceReview("travelogueDayPlaceReview");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Long> placeReviewId = createNumber("placeReviewId", Long.class);

    public final NumberPath<Long> travelogueDayId = createNumber("travelogueDayId", Long.class);

    public QTravelogueDayPlaceReview(String variable) {
        super(TravelogueDayPlaceReview.class, forVariable(variable));
    }

    public QTravelogueDayPlaceReview(Path<? extends TravelogueDayPlaceReview> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelogueDayPlaceReview(PathMetadata metadata) {
        super(TravelogueDayPlaceReview.class, metadata);
    }

}

