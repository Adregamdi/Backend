package com.adregamdi.travel.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travel")
public class Travel extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;
    @Column
    private UUID memberId; // 회원 id
    @Column
    private LocalDate startDate; // 시작일
    @Column
    private LocalDate endDate; // 종료일
    @Column
    private String title; // 제목

    public Travel(CreateMyTravelRequest request, String memberId) {
        this.memberId = UUID.fromString(memberId);
        this.startDate = request.startDate();
        this.endDate = request.endDate();
        this.title = request.title();
    }
}
