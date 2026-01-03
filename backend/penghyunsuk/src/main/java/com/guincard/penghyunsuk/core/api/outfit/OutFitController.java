/*
package com.guincard.penghyunsuk.core.api.outfit;

import com.guincard.penghyunsuk.core.api.outfit.outfitrequest.OutFitRequest;
import com.guincard.penghyunsuk.core.domain.outfit.service.OutFitService;
import com.guincard.penghyunsuk.core.support.response.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutFitController {

    OutFitService outFitService;
    // 날씨별 OOTD찾기
    @GetMapping
    public ApiResult<?> checkOutFit(int weatherNum, double temperature) {
        outFitService.FindOutFit(weatherNum, temperature);
        return ApiResult.success();
    }

    @PostMapping
    public ApiResult<?> inputOutFit(@RequestBody OutFitRequest outFitRequest) {
        outFitService.saveOutFit(outFitRequest);
        return ApiResult.success();
    }
}
*/
