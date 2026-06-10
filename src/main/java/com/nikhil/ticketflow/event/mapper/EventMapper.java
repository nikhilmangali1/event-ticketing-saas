package com.nikhil.ticketflow.event.mapper;

import com.nikhil.ticketflow.event.dto.request.CreateEventRequest;
import com.nikhil.ticketflow.event.dto.response.EventResponse;
import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.users.entity.UserEntity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventMapper {
    public EventResponse toResponse(EventEntity entity, UserEntity organizer){
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
                .build();
    }

    public EventEntity toEntity(@Valid CreateEventRequest eventRequest, UserEntity organizer) {
        EventEntity entity = new EventEntity();
        entity.setTitle(eventRequest.getTitle());
        entity.setOrganizer(organizer);
        entity.setDescription(eventRequest.getDescription());
        entity.setEventDate(eventRequest.getEventDate());
        entity.setVenue(eventRequest.getVenue());
        entity.setTotalSeats(eventRequest.getTotalSeats());
        entity.setAvailableSeats(eventRequest.getTotalSeats());
        entity.setPrice(eventRequest.getPrice());
        return entity;
    }
}
