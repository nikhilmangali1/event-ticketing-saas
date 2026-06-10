package com.nikhil.ticketflow.event.controller;

import com.nikhil.ticketflow.event.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.event.dto.request.UpdateEventRequest;
import com.nikhil.ticketflow.event.dto.response.EventResponse;
import com.nikhil.ticketflow.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest eventRequest){
        return ResponseEntity.ok(eventService.createEvent(eventRequest));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getAllEvents(){
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID eventId){
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteEventById(@PathVariable UUID eventId){
        eventService.deleteEventById(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable UUID eventId, @RequestBody UpdateEventRequest updateEventRequest){
        return ResponseEntity.ok(eventService.updateEventById(eventId, updateEventRequest));
    }
}
