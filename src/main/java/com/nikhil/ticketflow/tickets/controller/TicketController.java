package com.nikhil.ticketflow.tickets.controller;

import com.nikhil.ticketflow.tickets.dto.response.TicketBookedResponse;
import com.nikhil.ticketflow.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/book/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TicketBookedResponse> bookTicketsForAnEvent(@PathVariable UUID eventId){
        return ResponseEntity.ok(ticketService.bookTicket(eventId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TicketBookedResponse>> getMyTickets(){
        return ResponseEntity.ok(ticketService.getMyTickets());
    }

    @PatchMapping("/cancel/{ticketId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelTicket(@PathVariable UUID ticketId){
        ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok().build();
    }
}
