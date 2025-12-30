package com.guincard.penghyunsuk.core.domain.auth.Repository;

import com.guincard.penghyunsuk.core.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByLoginId(String loginId);
}
