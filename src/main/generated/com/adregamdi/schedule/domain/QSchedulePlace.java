package com.adregamdi.schedule.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedulePlace is a Querydsl query type for SchedulePlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedulePlace extends EntityPathBase<SchedulePlace> {

    private static final long serialVersionUID = -1895890364L;

    public static final QSchedulePlace schedulePlace = new QSchedulePlace("schedulePlace");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSchedulePlace(String variable) {
        super(SchedulePlace.class, forVariable(variable));
    }

    public QSchedulePlace(Path<? extends SchedulePlace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedulePlace(PathMetadata metadata) {
        super(SchedulePlace.class, metadata);
    }

}

