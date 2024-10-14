package com.adregamdi.travelogue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelogueDay is a Querydsl query type for TravelogueDay
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelogueDay extends EntityPathBase<TravelogueDay> {

    private static final long serialVersionUID = 1156775555L;

    public static final QTravelogueDay travelogueDay = new QTravelogueDay("travelogueDay");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Integer> day = createNumber("day", Integer.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<Long> travelogueDayId = createNumber("travelogueDayId", Long.class);

    public final NumberPath<Long> travelogueId = createNumber("travelogueId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTravelogueDay(String variable) {
        super(TravelogueDay.class, forVariable(variable));
    }

    public QTravelogueDay(Path<? extends TravelogueDay> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelogueDay(PathMetadata metadata) {
        super(TravelogueDay.class, metadata);
    }

}

