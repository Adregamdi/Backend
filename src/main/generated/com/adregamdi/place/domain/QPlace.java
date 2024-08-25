package com.adregamdi.place.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlace is a Querydsl query type for Place
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = 1527793853L;

    public static final QPlace place = new QPlace("place");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final StringPath address = createString("address");

    public final StringPath contentsLabel = createString("contentsLabel");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath imgPath = createString("imgPath");

    public final StringPath information = createString("information");

    public final StringPath introduction = createString("introduction");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath phoneNo = createString("phoneNo");

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final StringPath region1Cd = createString("region1Cd");

    public final StringPath region2Cd = createString("region2Cd");

    public final StringPath regionLabel = createString("regionLabel");

    public final StringPath roadAddress = createString("roadAddress");

    public final StringPath tag = createString("tag");

    public final StringPath thumbnailPath = createString("thumbnailPath");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPlace(String variable) {
        super(Place.class, forVariable(variable));
    }

    public QPlace(Path<? extends Place> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlace(PathMetadata metadata) {
        super(Place.class, metadata);
    }

}

