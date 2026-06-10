package com.nikhil.ticketflow.auth.repository;

import com.nikhil.ticketflow.auth.entity.RefreshTokenEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
    void deleteAllByUser(UserEntity user);
}
