package com.nikhil.ticketflow.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String userId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String role;
}
