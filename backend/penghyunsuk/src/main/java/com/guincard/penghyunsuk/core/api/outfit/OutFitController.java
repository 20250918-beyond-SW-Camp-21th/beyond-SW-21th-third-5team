package com.guincard.penghyunsuk.core.api.outfit;

import com.guincard.penghyunsuk.core.api.outfit.outfitrequest.OutFitRequest;
import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutfitDetailResponse;
import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutfitListResponse;
import com.guincard.penghyunsuk.core.domain.outfit.service.OutFitService;
import com.guincard.penghyunsuk.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outfit")
@RequiredArgsConstructor
public class OutFitController {

    private final OutFitService outFitService;

    @PostMapping
    public ResponseEntity<Void> saveOutfit(
            @RequestBody OutFitRequest request,
            Authentication authentication
    ){
        Long userId = outFitService.getUserId(authentication.getName());
        outFitService.saveOotd(userId,request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OutfitListResponse>> getOutfits(
            Authentication authentication
    ) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(outFitService.getMyOutfits(loginId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutfitDetailResponse> getOutfitDetail(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(outFitService.getMyOutfitDetail(loginId, id));
    }
}
