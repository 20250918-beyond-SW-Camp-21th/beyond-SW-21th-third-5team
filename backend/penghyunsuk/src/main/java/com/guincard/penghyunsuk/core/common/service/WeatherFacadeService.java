package com.guincard.penghyunsuk.core.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.guincard.penghyunsuk.core.common.dto.GridDTO;
import com.guincard.penghyunsuk.core.common.method.KmaGridConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    public String buildRecommendedOutfit(JsonNode items) {
        return buildRecommendedOutfit(items, Collections.emptyList());
    }

    public String buildRecommendedOutfit(JsonNode items, List<String> manualOutfits) {
        Double temperature = extractTemperature(items);
        String baseOutfit = temperature == null ? "" : outfitByTemperature(temperature);

        List<String> combined = new ArrayList<>();
        if (!baseOutfit.isBlank()) {
            combined.add(baseOutfit);
        }
        if (manualOutfits != null) {
            for (String outfit : manualOutfits) {
                if (outfit != null && !outfit.isBlank()) {
                    combined.add(outfit.trim());
                }
            }
        }
        return String.join(" + ", combined);
    }

    public Double extractForecastValue(JsonNode items, String... categories) {
        if (items == null || !items.isArray() || categories == null || categories.length == 0) {
            return null;
        }
        Set<String> categorySet = Set.of(categories);
        for (JsonNode item : items) {
            String category = item.path("category").asText();
            if (!categorySet.contains(category)) {
                continue;
            }
            String value = item.path("fcstValue").asText();
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private Double extractTemperature(JsonNode items) {
        if (items == null || !items.isArray()) {
            return null;
        }
        for (JsonNode item : items) {
            String category = item.path("category").asText();
            if (!"TMP".equals(category) && !"T1H".equals(category)) {
                continue;
            }
            String value = item.path("fcstValue").asText();
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String outfitByTemperature(double temp) {
        if (temp <= 5) return "패딩 + 목도리";
        if (temp <= 10) return "코트 + 니트";
        if (temp <= 16) return "자켓 + 긴바지";
        if (temp <= 22) return "가디건 + 긴바지";
        if (temp <= 27) return "반팔 + 얇은 겉옷";
        return "반팔 + 반바지";
    }
}
