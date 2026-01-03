package com.guincard.penghyunsuk.core.api.outfit.outfitrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutFitRequest {
    Long id;
    int weatherNum;
    double temperature;
    String imageUrl;
    String outFitReview;
}
