package com.nikhil.ticketflow.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String name;
    private String email;
}
