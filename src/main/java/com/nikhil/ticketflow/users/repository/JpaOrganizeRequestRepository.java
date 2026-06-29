package com.nikhil.ticketflow.users.repository;

import com.nikhil.ticketflow.users.entity.OrganizerRequestEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.OrganizerRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOrganizeRequestRepository extends JpaRepository<OrganizerRequestEntity, UUID> {
    boolean existsByUserAndStatus(UserEntity user, OrganizerRequestStatus organizerRequestStatus);

    Optional<OrganizerRequestEntity> findByUser(UserEntity user);
}
