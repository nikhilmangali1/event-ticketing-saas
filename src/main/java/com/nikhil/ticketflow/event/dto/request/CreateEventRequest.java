package com.nikhil.ticketflow.event.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    @NotBlank
    private String title;
    private String description;

    @NotBlank
    private String venue;

    @Future
    private LocalDateTime eventDate;
    private Integer totalSeats;

    @Positive
    private BigDecimal price;
}
