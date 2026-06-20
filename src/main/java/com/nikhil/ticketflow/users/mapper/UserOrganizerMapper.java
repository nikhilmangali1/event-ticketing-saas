package com.nikhil.ticketflow.users.mapper;

import com.nikhil.ticketflow.users.dto.response.UserOrganizerResponse;
import com.nikhil.ticketflow.users.entity.OrganizerRequestEntity;
import org.springframework.stereotype.Component;

@Component
public class UserOrganizerMapper {

    public UserOrganizerResponse toUserOrganizerResponse(OrganizerRequestEntity entity) {
        return UserOrganizerResponse.builder()
                .requestId(entity.getId())
                .userId(entity.getUser().getId())
                .userEmail(entity.getUser().getEmail())
                .reason(entity.getReason())
                .status(entity.getStatus().name())
                .requestedAt(entity.getRequestedAt())
                .reviewedAt(entity.getReviewedAt())
                .build();
    }
}
