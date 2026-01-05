package com.guincard.penghyunsuk.core.api.outfit.outfitresponse;

import java.time.LocalDate;

public record OutfitListResponse(
        Long id,
        LocalDate date,
        String imageUrl
) {
}
