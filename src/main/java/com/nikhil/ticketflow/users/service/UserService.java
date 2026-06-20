package com.nikhil.ticketflow.users.service;

import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import com.nikhil.ticketflow.common.exceptions.ResourceNotFoundException;
import com.nikhil.ticketflow.security.CurrentUser;
import com.nikhil.ticketflow.users.dto.response.UserOrganizerResponse;
import com.nikhil.ticketflow.users.entity.OrganizerRequestEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.OrganizerRequestStatus;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.mapper.UserOrganizerMapper;
import com.nikhil.ticketflow.users.repository.JpaOrganizeRequestRepository;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;
    private final CurrentUser currentUser;
    private final JpaOrganizeRequestRepository organizeRequestRepository;
    private final UserOrganizerMapper organizerMapper;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserOrganizerResponse requestOrganizerRole(String reason) {
        UUID userId = currentUser.getUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (user.getRole().equals(UserRole.ORGANIZER)) {
            throw new BadRequestException("You are already organizer");
        }

        if (organizeRequestRepository.existsByUserAndStatus(user, OrganizerRequestStatus.PENDING)) {
            throw new BadRequestException("already submitted");
        }

        if (organizeRequestRepository.existsByUserAndStatus(user, OrganizerRequestStatus.APPROVED)) {
            throw new BadRequestException("already approved");
        }

        OrganizerRequestEntity entity = OrganizerRequestEntity.builder()
                .reason(reason)
                .user(user)
                .status(OrganizerRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .reviewedAt(null)
                .build();

        OrganizerRequestEntity savedEntity = organizeRequestRepository.save(entity);

        return organizerMapper.toUserOrganizerResponse(savedEntity);
    }
}
