package com.nikhil.ticketflow.events.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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

    @PositiveOrZero
    private BigDecimal price;

    private MultipartFile image;
}
