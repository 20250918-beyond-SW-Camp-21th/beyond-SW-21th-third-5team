package com.guincard.penghyunsuk.core.domain.outfit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Table(name = "OutFit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OutFit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 ID")
    private Long id;

    @Column(nullable = false)
    @Comment("저장 날짜")
    private LocalDate saveDate = LocalDate.now();

    @Column(nullable = false)
    @Comment("날씨 번호")
    private Integer weatherNum;

    @Column(nullable = false)
    @Comment("체감 온도 평균")
    private Double temperature;

    @Column(nullable = false)
    @Comment("이미지 URL")
    private String imageUrl;

    @Column(nullable = false)
    @Comment("날씨 후기")
    private String outFitReview;

}
