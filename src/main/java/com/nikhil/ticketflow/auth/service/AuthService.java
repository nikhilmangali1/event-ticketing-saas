package com.nikhil.ticketflow.auth.service;

import com.nikhil.ticketflow.auth.dto.request.LoginRequest;
import com.nikhil.ticketflow.auth.dto.request.RegisterRequest;
import com.nikhil.ticketflow.auth.dto.response.LoginResponse;
import com.nikhil.ticketflow.auth.dto.response.RegisterResponse;
import com.nikhil.ticketflow.auth.repository.JpaRefreshTokenRepository;
import com.nikhil.ticketflow.auth.repository.JpaUserCredentialsRepository;
import com.nikhil.ticketflow.entity.RefreshTokenEntity;
import com.nikhil.ticketflow.entity.UserCredentialsEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final JpaUserCredentialsRepository userCredentialsRepository;
    private final JpaRefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RegisterResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered!");
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
                .createdAt(LocalDateTime.now())
                .build();

        userCredentialsRepository.save(credentials);

        return RegisterResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional
    public LoginResponse login(@Valid LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        UserCredentialsEntity credentials = userCredentialsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Credentials not found"));

        if(!encoder.matches(request.getPassword(), credentials.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
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
}
