package com.nikhil.ticketflow.security;

import com.nikhil.ticketflow.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("----------------------------JWT Filter hit-----------------------------");
        String token = resolveToken(request);
        if(token != null && jwtService.isTokenValid(token)){
            Claims claims = jwtService.extractClaims(token);
            UUID userId = UUID.fromString(claims.getSubject());
            String userRole = claims.get("role", String.class);
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_"+userRole));
            System.out.println("ROLE = " + userRole);
            System.out.println("AUTHORITIES = " + authorities);

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userId, userRole, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }
        return null;
    }
}
