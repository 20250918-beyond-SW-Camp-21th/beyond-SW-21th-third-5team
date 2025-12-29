package com.guincard.penghyunsuk.core.common.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.guincard.penghyunsuk.core.common.service.WeatherFacadeService;
import com.guincard.penghyunsuk.core.common.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherFacadeService weatherFacadeService;

    /**
     * 예:
     *  /api/weather/info?nx=64&ny=123
     *  /api/weather/info?nx=64&ny=123&baseDate=20240702&baseTime=1700
     */
    @GetMapping("/info")
    public ResponseEntity<JsonNode> getForecast(
            @RequestParam Integer nx,
            @RequestParam Integer ny,
            @RequestParam(required = false) String baseDate,
            @RequestParam(required = false) String baseTime
    ) {
        JsonNode items = weatherService.getForecast(nx, ny, baseDate, baseTime);
        return ResponseEntity.ok(items);
    }

    // 예: /api/weather/sht/by-location?lat=37.5665&lon=126.9780
    @GetMapping("/sht/by-location")
    public ResponseEntity<JsonNode> getShtByLocation(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String baseDate,
            @RequestParam(required = false) String baseTime
    ) {
        JsonNode items = weatherFacadeService.getShortTermForecastByLatLon(lat, lon, baseDate, baseTime);
        return ResponseEntity.ok(items);
    }

}

