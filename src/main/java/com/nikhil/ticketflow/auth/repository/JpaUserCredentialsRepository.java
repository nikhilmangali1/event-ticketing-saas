package com.nikhil.ticketflow.auth.repository;

import com.nikhil.ticketflow.auth.entity.UserCredentialsEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserCredentialsRepository extends JpaRepository<UserCredentialsEntity, UUID> {
    Optional<UserCredentialsEntity> findByUser(UserEntity user);
}
