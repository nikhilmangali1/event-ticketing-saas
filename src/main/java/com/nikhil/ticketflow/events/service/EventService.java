package com.nikhil.ticketflow.events.service;

import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import com.nikhil.ticketflow.common.exceptions.ResourceNotFoundException;
import com.nikhil.ticketflow.common.service.S3FileService;
import com.nikhil.ticketflow.events.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.events.dto.request.UpdateEventRequest;
import com.nikhil.ticketflow.events.dto.response.EventResponse;
import com.nikhil.ticketflow.events.entity.EventEntity;
import com.nikhil.ticketflow.events.mapper.EventMapper;
import com.nikhil.ticketflow.events.repository.JpaEventRepository;
import com.nikhil.ticketflow.security.CurrentUser;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final JpaEventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CurrentUser currentUser;
    private final JpaUserRepository userRepository;
    private final S3FileService s3FileService;

    @Transactional
    public EventResponse createEvent(@Valid CreateEventRequest eventRequest) throws IOException {
        UUID userId = currentUser.getUserId();
        UserEntity organizer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String imageUrl = null;
        if (eventRequest.getImage() != null && !eventRequest.getImage().isEmpty()) {
            imageUrl = s3FileService.uploadEventImage(eventRequest.getImage());
        }
        EventEntity entity = eventMapper.toEntity(eventRequest, organizer, imageUrl);
        EventEntity savedEvent = eventRepository.save(entity);
        return eventMapper.toResponse(savedEvent, organizer);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        List<EventEntity> events = eventRepository.findAll();
        return events.stream()
                .map(event -> eventMapper.toResponse(event, event.getOrganizer()))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return eventMapper.toResponse(eventEntity, eventEntity.getOrganizer());
    }

    @Transactional
    public void deleteEventById(UUID eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID currentUserId = currentUser.getUserId();
        if (!eventEntity.getOrganizer().getId().equals(currentUserId)) {
            throw new BadRequestException("You don't have access to delete this event");
        }
        eventRepository.delete(eventEntity);
    }

    @Transactional
    public EventResponse updateEventById(UUID eventId, UpdateEventRequest request) throws IOException {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID currentUserId = currentUser.getUserId();
        log.info("current user id: {}\norganizerId: {}", currentUserId, eventEntity.getOrganizer().getId());
        if (!eventEntity.getOrganizer().getId().equals(currentUserId)) {
            throw new BadRequestException("You don't have access to edit this event");
        }

        if (request.getTitle() != null) {
            eventEntity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            eventEntity.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            eventEntity.setEventDate(request.getEventDate());
        }
        if (request.getVenue() != null) {
            eventEntity.setVenue(request.getVenue());
        }
        if (request.getPrice() != null) {
            eventEntity.setPrice(request.getPrice());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = s3FileService.uploadEventImage(request.getImage());
            eventEntity.setImageUrl(imageUrl);
        }

        EventEntity updatedEvent = eventRepository.save(eventEntity);
        return eventMapper.toResponse(updatedEvent, updatedEvent.getOrganizer());

    }
}
