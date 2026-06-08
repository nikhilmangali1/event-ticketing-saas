package com.nikhil.ticketflow.auth.service;

import com.nikhil.ticketflow.auth.dto.request.LoginRequest;
import com.nikhil.ticketflow.auth.dto.request.RegisterRequest;
import com.nikhil.ticketflow.auth.dto.response.LoginResponse;
import com.nikhil.ticketflow.auth.dto.response.RegisterResponse;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public RegisterResponse register(@Valid RegisterRequest request) {


        log.info("--------register endpoint hit------");
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered!");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(encoder.encode(request.getPassword()))
                .role(UserRole.USER.name())
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    public LoginResponse login(@Valid LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(!encoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
