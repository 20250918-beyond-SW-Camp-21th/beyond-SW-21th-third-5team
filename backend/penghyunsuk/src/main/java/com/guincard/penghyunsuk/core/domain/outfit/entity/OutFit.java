package com.guincard.penghyunsuk.core.domain.outfit.entity;

import com.guincard.penghyunsuk.core.domain.outfit.OutfitEnum;
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
@NoArgsConstructor
@AllArgsConstructor
public class OutFit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 ID")
    private Long id;

    @Column(nullable = false)
    @Comment("사용자")
    private Long userId;

    @Column(nullable = false)
    @Comment("저장 날짜")
    private LocalDate saveDate = LocalDate.now();

    @Column(nullable = false)
    @Comment("날씨 번호")
    private Integer weatherNum; //PTY

    @Column(nullable = false)
    @Comment("최고기온")
    private Double temperature; //TMX

    @Column(nullable = false)
    @Comment("이미지 URL")
    private String imageUrl;

    @Column(nullable = false)
    @Comment("날씨 후기")
    private String outFitReview;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("만족도")
    private OutfitEnum outfit;

}
