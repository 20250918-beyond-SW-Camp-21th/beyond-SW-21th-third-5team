/*
package com.guincard.penghyunsuk.core.domain.outfit.service;

import com.guincard.penghyunsuk.core.api.outfit.outfitrequest.OutFitRequest;
import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutFitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OutFitService {

    OutFitRepository outFitRepository;

    public OutFitResponse FindOutFit(int weatherNum, double temperature) {
        OutFitResponse outFitResponse = outFitRepository.findByWeatherNum(weatherNum);
        if(isInRange(outFitResponse.getTemperature(),temperature)){
            return outFitResponse;
        }
        else{
            return null;
        }
    }

    public static boolean isInRange(double beforeTemp, double todayTemp) {
        double min = beforeTemp - 2;
        double max = beforeTemp + 2;
        return min <= todayTemp && max >= todayTemp;
    }

    public void saveOutFit(OutFitRequest request) {
        OutFit outFit = new OutFit(
                request.getId(),
                LocalDate.now(),
                request.getWeatherNum(),
                request.getTemperature(),
                request.getImageUrl(),
                request.getOutFitReview()
        );

        outFitRepository.save(outFit);
    }

}
*/
