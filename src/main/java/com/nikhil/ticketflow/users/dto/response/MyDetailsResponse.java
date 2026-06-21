package com.nikhil.ticketflow.users.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyDetailsResponse {
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
