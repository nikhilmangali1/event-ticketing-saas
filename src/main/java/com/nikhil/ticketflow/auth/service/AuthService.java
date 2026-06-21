package com.nikhil.ticketflow.auth.service;

import com.nikhil.ticketflow.auth.dto.request.LoginRequest;
import com.nikhil.ticketflow.auth.dto.request.LogoutRequest;
import com.nikhil.ticketflow.auth.dto.request.RefreshTokenRequest;
import com.nikhil.ticketflow.auth.dto.request.RegisterRequest;
import com.nikhil.ticketflow.auth.dto.response.LoginResponse;
import com.nikhil.ticketflow.auth.dto.response.RegisterResponse;
import com.nikhil.ticketflow.auth.entity.RefreshTokenEntity;
import com.nikhil.ticketflow.auth.entity.UserCredentialsEntity;
import com.nikhil.ticketflow.auth.repository.JpaRefreshTokenRepository;
import com.nikhil.ticketflow.auth.repository.JpaUserCredentialsRepository;
import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import com.nikhil.ticketflow.common.exceptions.ResourceNotFoundException;
import com.nikhil.ticketflow.email.service.EmailService;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final JpaUserCredentialsRepository userCredentialsRepository;
    private final JpaRefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;

    @Transactional
    public RegisterResponse register(@Valid RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered!");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity savedUser = userRepository.save(user);

        UserCredentialsEntity credentials = UserCredentialsEntity.builder()
                .user(savedUser)
                .passwordHash(encoder.encode(request.getPassword()))
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        emailService.sendHtmlEmail(
                savedUser.getEmail(),
                "Welcome to TicketFlow",
                "welcome",
                Map.of("name", savedUser.getName())
        );

        userCredentialsRepository.save(credentials);

        return RegisterResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional
    public LoginResponse login(@Valid LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        UserCredentialsEntity credentials = userCredentialsRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials not found"));

        if (!encoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.deleteAllByUser(user);
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshValidity()))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build());
        String userId = jwtService.extractUserId(accessToken).toString();

        return LoginResponse.builder()
                .userId(userId)
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public LoginResponse refreshToken(@Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadRequestException("Invalid refreshToken token");
        }

        Claims claims = jwtService.extractClaims(refreshToken);
        String tokenType = claims.get("tokenType", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new RuntimeException("Invalid token type");
        }

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("refreshToken token not found"));

        if (tokenEntity.getRevoked()) {
            throw new BadRequestException("Refresh token revoked");
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        UserEntity user = tokenEntity.getUser();

        String newAccessToken = jwtService.generateToken(user);
        refreshTokenRepository.delete(tokenEntity);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .user(user)
                .refreshToken(newRefreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshValidity()))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build());

        return LoginResponse.builder()
                .userId(user.getId().toString())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void logout(@Valid LogoutRequest request) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshTokenRepository.delete(tokenEntity);
    }
}
