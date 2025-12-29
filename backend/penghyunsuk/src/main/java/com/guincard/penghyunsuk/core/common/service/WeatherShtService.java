package com.guincard.penghyunsuk.core.common.service;

import com.guincard.penghyunsuk.core.common.entity.WeatherSht;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherShtService {

    private final List<WeatherSht> weatherList = new ArrayList<>();

    @Value("${weather.servicekey}")
    private String serviceKey;


    @Value("${weahter.sht.vilage.url}")
    private String shtVilageUrl;


    public String urlParam(String serviceKey) throws UnsupportedEncodingException {

        String urlParams = "?serviceKey=" + URLEncoder.encode(serviceKey, "UTF-8")
                + "&pageNo=1&numOfRows=1000&dataType=JSON&base_date=" + baseDate + "&base_time=" + baseTime + "&nx=64&ny=123";

    }
}