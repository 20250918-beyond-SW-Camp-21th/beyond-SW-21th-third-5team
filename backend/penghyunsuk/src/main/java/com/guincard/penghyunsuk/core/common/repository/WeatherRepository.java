package com.guincard.penghyunsuk.common.repository;

import com.guincard.penghyunsuk.common.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    List<Weather> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
