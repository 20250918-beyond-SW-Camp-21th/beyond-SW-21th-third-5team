package com.guincard.penghyunsuk.core.api.map.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record SidoWeatherResponse(JsonNode items, String recommnededOutfit) {}
