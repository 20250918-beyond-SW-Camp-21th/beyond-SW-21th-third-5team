package com.guincard.penghyunsuk.core.common.service;

import jakarta.annotation.PostConstruct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class WeatherShtService {

    @Value("${weather.servicekey}")
    private String serviceKey;


    @Value("${weather.sht.vilage.url}")
    private String shtVilageUrl;

    @PostConstruct
    public void start() throws IOException {
        String str = getWeatherJson(shtVilageUrl, urlParam(serviceKey));

        JSONArray jsonArray = WeatherJsonArray(str);
        for (Object o : jsonArray) {
            System.out.println("Json = " + o);
        }
    }


    public String urlParam(String serviceKey) throws UnsupportedEncodingException {

        return "?serviceKey=" + URLEncoder.encode(serviceKey, "UTF-8")
                + "&pageNo=1&numOfRows=1000&dataType=JSON&base_date=" + "20240702" + "&base_time=" + "1700" + "&nx=64&ny=123";

    }

    public String getWeatherJson(String urlLink, String urlParams) throws IOException {

        URL url = new URL(urlLink + urlParams);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public JSONArray WeatherJsonArray(String response){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONObject responseObject = (JSONObject) jsonObject.get("response");
            JSONObject bodyObject = (JSONObject) responseObject.get("body");
            JSONObject itemsObject = (JSONObject) bodyObject.get("items");
            return (JSONArray) itemsObject.get("item");
        } catch (ParseException e){
            return null;
        }
    }
}
