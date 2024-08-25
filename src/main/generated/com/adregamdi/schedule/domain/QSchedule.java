package com.adregamdi.schedule.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = 96321667L;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath endDate = createString("endDate");

    public final ComparablePath<java.util.UUID> memberId = createComparable("memberId", java.util.UUID.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public final StringPath startDate = createString("startDate");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSchedule(String variable) {
        super(Schedule.class, forVariable(variable));
    }

    public QSchedule(Path<? extends Schedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedule(PathMetadata metadata) {
        super(Schedule.class, metadata);
    }

}

