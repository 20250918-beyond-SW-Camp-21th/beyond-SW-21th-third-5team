package com.guincard.penghyunsuk.core.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.serviceKey}")
    private String serviceKey;

    @Value("${weather.sht.url}")
    private String shtUrl;

    /**
     * 단기예보 조회
     * - baseDate/baseTime이 없으면 "현재 기준"으로 가장 최근 발표 시각을 자동 계산해 요청함.
     */
    public JsonNode getForecast(Integer nx, Integer ny, String baseDate, String baseTime) {
        if (nx == null || ny == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nx, ny는 필수입니다.");
        }

        BaseDateTime dt = resolveBaseDateTime(baseDate, baseTime);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(shtUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", dt.baseDate)
                .queryParam("base_time", dt.baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        String body = webClientBuilder.build()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractItems(body);
    }

    /**
     * 응답 JSON에서 items.item 배열을 JsonNode로 뽑아 반환
     */
    private JsonNode extractItems(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);

            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText();
            String resultMsg = header.path("resultMsg").asText();

            if (!"00".equals(resultCode)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "기상청 API 오류: " + resultCode + " / " + resultMsg
                );
            }

            JsonNode items = root.path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            // item이 배열이 아니면 비정상
            if (!items.isArray()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "응답 items.item이 배열이 아닙니다.");
            }
            return items;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    /**
     * baseDate/baseTime이 주어지면 그대로 사용, 없으면 현재 기준으로 가장 최근 발표 시각 계산.
     * 단기예보 base_time 후보: 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
     *
     * 실무에서는 발표 직후 데이터 반영 지연을 고려해 "현재 - 30분" 기준으로 잡는 경우가 많아
     * 여기서는 now-30min 기준으로 가장 최근 base_time을 선택함.
     */
    private BaseDateTime resolveBaseDateTime(String baseDate, String baseTime) {
        if (baseDate != null && baseTime != null) {
            // 사용자가 준 값 사용 (형식 검증 최소)
            return new BaseDateTime(baseDate, baseTime);
        }

        LocalDateTime now = LocalDateTime.now().minusMinutes(30);
        LocalDate date = now.toLocalDate();
        int hhmm = now.getHour() * 100 + now.getMinute();

        List<Integer> baseTimes = List.of(2300, 2000, 1700, 1400, 1100, 800, 500, 200);

        Integer picked = null;
        for (int bt : baseTimes) {
            if (hhmm >= bt) {
                picked = bt;
                break;
            }
        }

        // 새벽(00:xx~01:xx)면 전날 2300 사용
        if (picked == null) {
            date = date.minusDays(1);
            picked = 2300;
        }

        String resolvedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String resolvedTime = String.format("%04d", picked);

        return new BaseDateTime(resolvedDate, resolvedTime);
    }

    // 발표 날짜 + 시간을 묶어서 처리하기 위해.
    private record BaseDateTime(String baseDate, String baseTime) {}
}
