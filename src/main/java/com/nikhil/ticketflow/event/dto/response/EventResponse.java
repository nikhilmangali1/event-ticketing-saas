package com.nikhil.ticketflow.event.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventResponse {

    private UUID id;
    private String title;
    private String organizerName;
    private String organizerEmail;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
}
