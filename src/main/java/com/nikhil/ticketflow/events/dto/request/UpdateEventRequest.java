package com.nikhil.ticketflow.events.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String title;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private MultipartFile image;
}
