package com.guincard.penghyunsuk.core.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("날씨 데이터 ID")
    private Long id;

    @Column(nullable = false, updatable = false)
    @Comment("예보 발표 시간")
    private LocalDateTime baseTime;

    @Column(nullable = false, updatable = false)
    @Comment("실제 예측 시간")
    private LocalDateTime fcstTime;

    @Column(nullable = false)
    @Comment("격자좌표-X")
    private Double LOC_X;

    @Column(nullable = false)
    @Comment("격자좌표-Y")
    private Double LOC_Y;

    @Column(nullable = false)
    @Comment("기온")
    private Double TMP;

    @Column(nullable = false)
    @Comment("최고기온")
    private Double TMX;

    @Column(nullable = false)
    @Comment("최저기온")
    private Double TMN;

    @Column(nullable = false)
    @Comment("하늘상태")
    private Integer SKY;

    @Column(nullable = false)
    @Comment("강수형태")
    private Integer PTY;

    @Column(nullable = false)
    @Comment("강수유무")
    private Integer POP;

    @Column(nullable = false)
    @Comment("1시간 강수량")
    private Double PCP;

    @Column(nullable = false)
    @Comment("1시간 신적설")
    private Double SNO;

    @Column(nullable = false)
    @Comment("상대습도")
    private Integer REH;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @Builder

    public Weather(LocalDateTime baseTime, LocalDateTime fcstTime, Double LOC_X, Double LOC_Y, Double TMP, Double TMX, Double TMN, Integer SKY, Integer PTY, Integer POP, Double PCP, Double SNO, Integer REH) {
        this.baseTime = baseTime;
        this.fcstTime = fcstTime;
        this.LOC_X = LOC_X;
        this.LOC_Y = LOC_Y;
        this.TMP = TMP;
        this.TMX = TMX;
        this.TMN = TMN;
        this.SKY = SKY;
        this.PTY = PTY;
        this.POP = POP;
        this.PCP = PCP;
        this.SNO = SNO;
        this.REH = REH;
    }
}
