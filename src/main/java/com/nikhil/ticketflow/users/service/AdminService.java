package com.nikhil.ticketflow.users.service;

import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import com.nikhil.ticketflow.common.exceptions.ResourceNotFoundException;
import com.nikhil.ticketflow.users.dto.response.UserOrganizerResponse;
import com.nikhil.ticketflow.users.entity.OrganizerRequestEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.OrganizerRequestStatus;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.mapper.UserOrganizerMapper;
import com.nikhil.ticketflow.users.repository.JpaOrganizeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final JpaOrganizeRequestRepository organizeRequestRepository;
    private final UserOrganizerMapper organizerMapper;

    public List<UserOrganizerResponse> getAllRequests() {
        return organizeRequestRepository.findAll().stream()
                .map(organizerMapper::toUserOrganizerResponse)
                .toList();
    }

    public void approve(UUID requestId) {
        OrganizerRequestEntity requestEntity = organizeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("request not found"));

        if (requestEntity.getStatus() != OrganizerRequestStatus.PENDING) {
            throw new BadRequestException("invalid status");
        }

        UserEntity user = requestEntity.getUser();

        if (user.getRole().equals(UserRole.ORGANIZER)) {
            throw new BadRequestException("Already organizer");
        }

        user.setRole(UserRole.ORGANIZER);
        user.setUpdatedAt(LocalDateTime.now());

        requestEntity.setStatus(OrganizerRequestStatus.APPROVED);
        requestEntity.setRequestedAt(LocalDateTime.now());

        organizeRequestRepository.save(requestEntity);
    }

    public void reject(UUID requestId) {
        OrganizerRequestEntity requestEntity = organizeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("request not found"));

        if (requestEntity.getStatus() != OrganizerRequestStatus.PENDING) {
            throw new BadRequestException("invalid status");
        }

        UserEntity user = requestEntity.getUser();

        if (user.getRole().equals(UserRole.ORGANIZER)) {
            throw new BadRequestException("Already organizer");
        }

        requestEntity.setStatus(OrganizerRequestStatus.REJECTED);
        requestEntity.setReviewedAt(LocalDateTime.now());
        organizeRequestRepository.save(requestEntity);
    }
}
