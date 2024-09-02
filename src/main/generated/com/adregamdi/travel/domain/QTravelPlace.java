package com.adregamdi.travel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelPlace is a Querydsl query type for TravelPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelPlace extends EntityPathBase<TravelPlace> {

    private static final long serialVersionUID = -2043025154L;

    public static final QTravelPlace travelPlace = new QTravelPlace("travelPlace");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Integer> placeOrder = createNumber("placeOrder", Integer.class);

    public final NumberPath<Long> travelDayId = createNumber("travelDayId", Long.class);

    public final NumberPath<Long> travelPlaceId = createNumber("travelPlaceId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTravelPlace(String variable) {
        super(TravelPlace.class, forVariable(variable));
    }

    public QTravelPlace(Path<? extends TravelPlace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelPlace(PathMetadata metadata) {
        super(TravelPlace.class, metadata);
    }

}

