package com.guincard.penghyunsuk.core.api.map.dto.response;

public record SidoWeatherResponse(
        Double temperature,
        Double pop,
        Double wsd,
        Double reh,
        String recommnededOutfit
) {}
