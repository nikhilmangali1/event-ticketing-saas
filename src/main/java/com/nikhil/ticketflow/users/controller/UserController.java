package com.nikhil.ticketflow.users.controller;

import com.nikhil.ticketflow.users.dto.response.UserOrganizerResponse;
import com.nikhil.ticketflow.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/organizer-request")
    public ResponseEntity<UserOrganizerResponse> requestOrganizerRole(@RequestParam String reason) {
        return ResponseEntity.ok(userService.requestOrganizerRole(reason));
    }
}
