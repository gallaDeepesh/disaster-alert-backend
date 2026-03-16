package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.RefreshToken;
import com.disastermanagement.emergency_alert_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);

    @Override
    Optional<RefreshToken> findById(Long aLong);
    Optional<RefreshToken> findByUser(User user);



}

