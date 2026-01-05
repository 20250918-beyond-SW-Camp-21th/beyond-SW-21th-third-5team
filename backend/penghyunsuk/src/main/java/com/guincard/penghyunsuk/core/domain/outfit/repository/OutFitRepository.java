package com.guincard.penghyunsuk.core.domain.outfit.repository;

import com.guincard.penghyunsuk.core.domain.outfit.entity.OutFit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutFitRepository extends JpaRepository<OutFit,Long> {
    List<OutFit> findAllByUserIdOrderBySaveDateDesc(Long userId);

    Optional<OutFit> findByIdAndUserId(Long id, Long userId);
}
