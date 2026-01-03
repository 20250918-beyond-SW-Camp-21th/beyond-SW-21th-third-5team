package com.guincard.penghyunsuk.core.api.map.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.guincard.penghyunsuk.core.api.map.dto.response.SidoWeatherResponse;
import com.guincard.penghyunsuk.core.common.service.WeatherFacadeService;
import com.guincard.penghyunsuk.core.domain.map.enums.Sido;
import com.guincard.penghyunsuk.core.support.error.CoreException;
import com.guincard.penghyunsuk.core.support.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final WeatherFacadeService weatherFacadeService;


    /*
     * 17개 도시 날씨 정보 한번에
     * */
    @GetMapping("/sido")
    public ResponseEntity<Map<String, SidoWeatherResponse>> getWeatherForAllSido(
            @RequestParam(required = false) String baseDate,
            @RequestParam(required = false) String baseTime
    ) {
        Map<String, SidoWeatherResponse> result = new LinkedHashMap<>();
        for (Sido sido : Sido.values()) {
            JsonNode items = weatherFacadeService.getForecastByLatLon(
                    sido.getlat(),
                    sido.getlon(),
                    baseDate,
                    baseTime
            );
            String recommnededOutfit = weatherFacadeService.buildRecommendedOutfit(items);
            Double temperature = weatherFacadeService.extractForecastValue(items, "TMP", "T1H");
            Double pop = weatherFacadeService.extractForecastValue(items, "POP");
            Double wsd = weatherFacadeService.extractForecastValue(items, "WSD");
            Double reh = weatherFacadeService.extractForecastValue(items, "REH");
            result.put(
                    sido.getdisplayname(),
                    new SidoWeatherResponse(temperature, pop, wsd, reh, recommnededOutfit)
            );
        }
        return ResponseEntity.ok(result);
    }

    /*
     * 특정 도시 날씨 정보
     */
    @GetMapping("/sido/one")
    public ResponseEntity<SidoWeatherResponse> getWeatherBySido(
            @RequestParam String sido,
            @RequestParam(required = false) String baseDate,
            @RequestParam(required = false) String baseTime
    ) {
        Sido resolvedSido = resolveSido(sido);
        JsonNode items = weatherFacadeService.getForecastByLatLon(
                resolvedSido.getlat(),
                resolvedSido.getlon(),
                baseDate,
                baseTime
        );
        String recommnededOutfit = weatherFacadeService.buildRecommendedOutfit(items);
        Double temperature = weatherFacadeService.extractForecastValue(items, "TMP", "T1H");
        Double pop = weatherFacadeService.extractForecastValue(items, "POP");
        Double wsd = weatherFacadeService.extractForecastValue(items, "WSD");
        Double reh = weatherFacadeService.extractForecastValue(items, "REH");
        return ResponseEntity.ok(new SidoWeatherResponse(temperature, pop, wsd, reh, recommnededOutfit));
    }

    private Sido resolveSido(String sido) {
        Sido byDisplayName = Sido.fromdisplayname(sido);
        if (byDisplayName != null) {
            return byDisplayName;
        }
        try {
            return Sido.valueOf(sido.toLowerCase());
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 시도입니다.");
        }
    }
}
