package com.nikhil.ticketflow.events.controller;

import com.nikhil.ticketflow.common.service.S3FileService;
import com.nikhil.ticketflow.events.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.events.dto.request.UpdateEventRequest;
import com.nikhil.ticketflow.events.dto.response.EventResponse;
import com.nikhil.ticketflow.events.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final S3FileService s3FileService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @ModelAttribute CreateEventRequest eventRequest) throws IOException {
        return ResponseEntity.ok(eventService.createEvent(eventRequest));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('USER','ORGANIZER')")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> deleteEventById(@PathVariable UUID eventId) {
        eventService.deleteEventById(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable UUID eventId, @ModelAttribute UpdateEventRequest updateEventRequest) throws IOException {
        return ResponseEntity.ok(eventService.updateEventById(eventId, updateEventRequest));
    }

    @PostMapping("/test-upload")
    public String testUpload(
            @RequestParam MultipartFile file
    ) throws IOException {

        return s3FileService.uploadEventImage(file);
    }
}
