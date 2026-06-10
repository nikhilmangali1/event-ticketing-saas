package com.nikhil.ticketflow.event.service;

import com.nikhil.ticketflow.event.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.event.dto.request.UpdateEventRequest;
import com.nikhil.ticketflow.event.dto.response.EventResponse;
import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.event.mapper.EventMapper;
import com.nikhil.ticketflow.event.repository.JpaEventRepository;
import com.nikhil.ticketflow.security.CurrentUser;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final JpaEventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CurrentUser currentUser;
    private final JpaUserRepository userRepository;

    @Transactional
    public EventResponse createEvent(@Valid CreateEventRequest eventRequest) {
        UUID userId = currentUser.getUserId();
        UserEntity organizer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        EventEntity entity = eventMapper.toEntity(eventRequest, organizer);
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
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return eventMapper.toResponse(eventEntity, eventEntity.getOrganizer());
    }

    @Transactional
    public void deleteEventById(UUID eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        UUID currentUserId = currentUser.getUserId();
        if(!eventEntity.getOrganizer().getId().equals(currentUserId)){
            throw new RuntimeException("You don't have access to delete this event");
        }
        eventRepository.delete(eventEntity);
    }

    @Transactional
    public EventResponse updateEventById(UUID eventId, UpdateEventRequest request) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        UUID currentUserId = currentUser.getUserId();
        if(!eventEntity.getOrganizer().getId().equals(currentUserId)){
            throw new RuntimeException("You don't have access to edit this event");
        }

        if(request.getTitle() != null){
            eventEntity.setTitle(request.getTitle());
        }
        if(request.getDescription() != null){
            eventEntity.setDescription(request.getDescription());
        }
        if(request.getEventDate() != null){
            eventEntity.setEventDate(request.getEventDate());
        }
        if(request.getVenue() != null){
            eventEntity.setVenue(request.getVenue());
        }
        if(request.getPrice() != null){
            eventEntity.setPrice(request.getPrice());
        }

        EventEntity updatedEvent = eventRepository.save(eventEntity);
        return eventMapper.toResponse(updatedEvent, updatedEvent.getOrganizer());

    }
}
