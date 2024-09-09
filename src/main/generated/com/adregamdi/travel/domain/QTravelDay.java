package com.adregamdi.travel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelDay is a Querydsl query type for TravelDay
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelDay extends EntityPathBase<TravelDay> {

    private static final long serialVersionUID = -989846189L;

    public static final QTravelDay travelDay = new QTravelDay("travelDay");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Integer> day = createNumber("day", Integer.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<Long> travelDayId = createNumber("travelDayId", Long.class);

    public final NumberPath<Long> travelId = createNumber("travelogueId", Long.class);

    public QTravelDay(String variable) {
        super(TravelDay.class, forVariable(variable));
    }

    public QTravelDay(Path<? extends TravelDay> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelDay(PathMetadata metadata) {
        super(TravelDay.class, metadata);
    }

}

