package com.guincard.penghyunsuk.core.domain.auth;

import com.guincard.penghyunsuk.core.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);   // 로그인 id 로 로그인

    boolean existsByLoginId(String loginId);    // id로 중복체크

    boolean existsByEmail(String email);    // 이메일로 중복체크
}