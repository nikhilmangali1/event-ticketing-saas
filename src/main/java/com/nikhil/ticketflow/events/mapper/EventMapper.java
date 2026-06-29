package com.nikhil.ticketflow.events.mapper;

import com.nikhil.ticketflow.events.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.events.dto.response.EventResponse;
import com.nikhil.ticketflow.events.entity.EventEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventResponse toResponse(EventEntity entity, UserEntity organizer) {
        return EventResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .organizerName(organizer.getName())
                .organizerEmail(organizer.getEmail())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .venue(entity.getVenue())
                .totalSeats(entity.getTotalSeats())
                .availableSeats(entity.getAvailableSeats())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public EventEntity toEntity(@Valid CreateEventRequest eventRequest, UserEntity organizer, String imageUrl) {
        EventEntity entity = new EventEntity();
        entity.setTitle(eventRequest.getTitle());
        entity.setOrganizer(organizer);
        entity.setDescription(eventRequest.getDescription());
        entity.setEventDate(eventRequest.getEventDate());
        entity.setVenue(eventRequest.getVenue());
        entity.setTotalSeats(eventRequest.getTotalSeats());
        entity.setAvailableSeats(eventRequest.getTotalSeats());
        entity.setPrice(eventRequest.getPrice());
        entity.setImageUrl(imageUrl);
        return entity;
    }
}
