package com.guincard.penghyunsuk.core.api.outfit.outfitresponse;

import com.guincard.penghyunsuk.core.domain.outfit.OutfitEnum;

import java.time.LocalDate;

public record OutfitDetailResponse(
        Long id,
        LocalDate date,
        Integer weatherNum,
        Double temperature,
        String imageUrl,
        String outFitReview,
        OutfitEnum satisfaction
) {
}
