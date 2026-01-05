package com.guincard.penghyunsuk.core.api.outfit.outfitrequest;

import com.guincard.penghyunsuk.core.domain.outfit.OutfitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutFitRequest {
    int weatherNum;
    double temperature;
    String imageUrl;
    String outfitReview;
    LocalDate date;
    OutfitEnum satisfaction;
}
