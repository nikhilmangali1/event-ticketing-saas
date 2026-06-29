package com.nikhil.ticketflow.users.controller;

import com.nikhil.ticketflow.users.dto.response.UserOrganizerResponse;
import com.nikhil.ticketflow.users.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/user-organizer/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserOrganizerResponse>> getAllRequests() {
        return ResponseEntity.ok(adminService.getAllRequests());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user-organizer/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable UUID requestId) {
        adminService.approve(requestId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user-organizer/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable UUID requestId) {
        adminService.reject(requestId);
        return ResponseEntity.noContent().build();
    }
}
