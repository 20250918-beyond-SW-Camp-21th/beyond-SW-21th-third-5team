package com.guincard.penghyunsuk.core.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.guincard.penghyunsuk.core.common.dto.GridDTO;
import com.guincard.penghyunsuk.core.common.method.KmaGridConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherFacadeService {

    private final WeatherService weatherService;

    public JsonNode getForecastByLatLon(double lat, double lon, String baseDate, String baseTime) {
        // 위경도 -> 격자좌표
        GridDTO grid = KmaGridConverter.toGrid(lat, lon);

        // 격자좌표로 변환한 값으로 예보 조회
        return weatherService.getForecast(grid.nx(), grid.ny(), baseDate, baseTime);
    }
}
