package com.cab.olaapi.repository;

import com.cab.olaapi.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
