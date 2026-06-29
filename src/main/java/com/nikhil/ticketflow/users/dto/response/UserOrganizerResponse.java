package com.nikhil.ticketflow.users.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserOrganizerResponse {

    private UUID requestId;
    private UUID userId;
    private String userEmail;
    private String reason;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
}
