package com.adregamdi.travel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedulePlace is a Querydsl query type for SchedulePlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedulePlace extends EntityPathBase<TravelPlace> {

    private static final long serialVersionUID = -1895890364L;

    public static final QSchedulePlace schedulePlace = new QSchedulePlace("schedulePlace");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Integer> placeOrder = createNumber("placeOrder", Integer.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSchedulePlace(String variable) {
        super(TravelPlace.class, forVariable(variable));
    }

    public QSchedulePlace(Path<? extends TravelPlace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedulePlace(PathMetadata metadata) {
        super(TravelPlace.class, metadata);
    }

}
