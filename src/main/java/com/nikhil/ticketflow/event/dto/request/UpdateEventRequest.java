package com.nikhil.ticketflow.event.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String title;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private BigDecimal price;
}
