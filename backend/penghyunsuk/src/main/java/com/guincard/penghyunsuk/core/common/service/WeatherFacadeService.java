package com.guincard.penghyunsuk.core.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.guincard.penghyunsuk.core.common.dto.GridDTO;
import com.guincard.penghyunsuk.core.common.method.KmaGridConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherFacadeService {

    private final WeatherService weatherService; // (이미 만들어둔) 기상청 호출 서비스

    public JsonNode getShortTermForecastByLatLon(double lat, double lon, String baseDate, String baseTime) {
        GridDTO grid = KmaGridConverter.toGrid(lat, lon);

        // 기상청 단기예보는 nx, ny(격자 X/Y)를 요구 :contentReference[oaicite:1]{index=1}
        return weatherService.getForecast(grid.nx(), grid.ny(), baseDate, baseTime);
    }
}
