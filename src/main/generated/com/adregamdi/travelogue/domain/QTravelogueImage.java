package com.adregamdi.travelogue.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTravelogueImage is a Querydsl query type for TravelogueImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelogueImage extends EntityPathBase<TravelogueImage> {

    private static final long serialVersionUID = -730265982L;

    public static final QTravelogueImage travelogueImage = new QTravelogueImage("travelogueImage");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> travelogueId = createNumber("travelogueId", Long.class);

    public final NumberPath<Long> travelogueImageId = createNumber("travelogueImageId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath url = createString("url");

    public QTravelogueImage(String variable) {
        super(TravelogueImage.class, forVariable(variable));
    }

    public QTravelogueImage(Path<? extends TravelogueImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelogueImage(PathMetadata metadata) {
        super(TravelogueImage.class, metadata);
    }

}

