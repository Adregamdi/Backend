package com.adregamdi.travel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravel is a Querydsl query type for Travel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravel extends EntityPathBase<Travel> {

    private static final long serialVersionUID = 1881673097L;

    public static final QTravel travel = new QTravel("travel");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> day = createNumber("day", Integer.class);

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath memberId = createString("memberId");

    public final StringPath memo = createString("memo");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> travelId = createNumber("travelId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTravel(String variable) {
        super(Travel.class, forVariable(variable));
    }

    public QTravel(Path<? extends Travel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravel(PathMetadata metadata) {
        super(Travel.class, metadata);
    }

}

