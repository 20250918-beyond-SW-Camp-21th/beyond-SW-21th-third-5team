package com.guincard.penghyunsuk.core.domain.outfit;

import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutFitResponse;
import com.guincard.penghyunsuk.core.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutFitRepository extends JpaRepository<OutFit, Long> {

    OutFitResponse findByWeatherNum(int weatherNum);
}
