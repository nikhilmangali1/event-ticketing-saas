package com.nikhil.ticketflow.tickets.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventDetailsResponse {
    private UUID eventId;
    private String title;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
}
