package com.guincard.penghyunsuk.core.common.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherSht {

    private String POP;
    private String PTY;
    private String PCP;
    private String REH;
    private String SNO;
    private String SKY;
    private String TMP;
    private String TMN;
    private String TMX;


    private String fcstDate;    // 예상날자
    private String fcstTime;    // 예상 시간


}