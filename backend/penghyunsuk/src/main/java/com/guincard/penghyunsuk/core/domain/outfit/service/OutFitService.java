package com.guincard.penghyunsuk.core.domain.outfit.service;

import com.guincard.penghyunsuk.core.api.outfit.outfitrequest.OutFitRequest;
import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutfitDetailResponse;
import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutfitListResponse;
import com.guincard.penghyunsuk.core.common.service.WeatherFacadeService;
import com.guincard.penghyunsuk.core.common.service.WeatherService;
import com.guincard.penghyunsuk.core.domain.auth.Repository.UserRepository;
import com.guincard.penghyunsuk.core.domain.auth.entity.User;
import com.guincard.penghyunsuk.core.domain.outfit.entity.OutFit;
import com.guincard.penghyunsuk.core.domain.outfit.repository.OutFitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutFitService {

    private final OutFitRepository outFitRepository;
    private final UserRepository userRepository;

    public void saveOotd(Long userId,OutFitRequest request) {
        OutFit outFit = new OutFit(
            null,
            userId,
            request.getDate(),
            request.getWeatherNum(),
            request.getTemperature(),
            request.getImageUrl(),
            request.getOutfitReview(),
            request.getSatisfaction()
        );

        outFitRepository.save(outFit);
    }
    public Long getUserId(String userId) {
        return userRepository.findByLoginId(userId).map(User::getId).orElse(null);
    }

    public List<OutfitListResponse> getMyOutfits(String usdrId) {
        Long userId = userRepository.findByLoginId(usdrId).map(User::getId).orElse(null);

        return outFitRepository.findAllByUserIdOrderBySaveDateDesc(userId).stream()
                .map(o -> new OutfitListResponse(
                        o.getId(),
                        o.getSaveDate(),
                        o.getImageUrl()
                ))
                .toList();
    }
    public OutfitDetailResponse getMyOutfitDetail(String usdrId, Long outfitId) {
        Long userId = userRepository.findByLoginId(usdrId).map(User::getId).orElse(null);

        OutFit o = outFitRepository.findByIdAndUserId(outfitId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다. id=" + outfitId));

        return new OutfitDetailResponse(
                o.getId(),
                o.getSaveDate(),
                o.getWeatherNum(),
                o.getTemperature(),
                o.getImageUrl(),
                o.getOutFitReview(),
                o.getOutfit()
        );
    }

}
