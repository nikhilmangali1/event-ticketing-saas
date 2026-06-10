package com.nikhil.ticketflow.security;

import com.nikhil.ticketflow.users.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    public UUID getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof JwtAuthenticationToken jwtAuth){
            return (UUID) jwtAuth.getPrincipal();
        }
        return null;
    }

    public String getRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof JwtAuthenticationToken jwtAuth){
            return jwtAuth.getUserRole();
        }
        return null;
    }

    public boolean hasRole(String role){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_"+role));
    }
}
