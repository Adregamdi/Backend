package com.adregamdi.travelogue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelogue is a Querydsl query type for Travelogue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelogue extends EntityPathBase<Travelogue> {

    private static final long serialVersionUID = 1977041497L;

    public static final QTravelogue travelogue = new QTravelogue("travelogue");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath introduction = createString("introduction");

    public final StringPath memberId = createString("memberId");

    public final StringPath title = createString("title");

    public final NumberPath<Long> travelogueId = createNumber("travelogueId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTravelogue(String variable) {
        super(Travelogue.class, forVariable(variable));
    }

    public QTravelogue(Path<? extends Travelogue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelogue(PathMetadata metadata) {
        super(Travelogue.class, metadata);
    }

}

