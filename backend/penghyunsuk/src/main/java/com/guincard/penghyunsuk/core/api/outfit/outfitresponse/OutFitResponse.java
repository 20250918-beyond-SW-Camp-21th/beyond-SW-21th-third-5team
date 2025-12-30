package com.guincard.penghyunsuk.core.api.outfit.outfitresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutFitResponse {
    Integer weatherNum;
    Double temperature;
    String urlImage;
    String outFitReview;
}
