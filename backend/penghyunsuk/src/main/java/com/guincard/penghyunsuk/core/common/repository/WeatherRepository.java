package com.guincard.penghyunsuk.core.common.repository;

import com.guincard.penghyunsuk.core.common.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    List<Weather> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
