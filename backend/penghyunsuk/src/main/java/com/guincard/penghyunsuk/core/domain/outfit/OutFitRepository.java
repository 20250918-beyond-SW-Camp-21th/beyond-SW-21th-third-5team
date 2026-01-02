package com.guincard.penghyunsuk.core.domain.outfit;

import com.guincard.penghyunsuk.core.api.outfit.outfitresponse.OutFitResponse;
import com.guincard.penghyunsuk.core.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutFitRepository extends JpaRepository<OutFit, Long> {

    OutFitResponse findByWeatherNum(int weatherNum);

    @Query(
            """
    select 
        weatherNum,
        temperature,
        urlImage,
        outFitReview
        FROM OutFit 
        WHERE temperature + 2 < :value
        and temperature -2 > :value
        order by temperature
"""
    );
    OutFitResponse findByWeather(@Param("value") double temperature);
}
