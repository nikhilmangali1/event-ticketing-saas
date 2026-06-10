package com.nikhil.ticketflow.tickets.mapper;

import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.tickets.dto.response.EventDetailsResponse;
import com.nikhil.ticketflow.tickets.dto.response.TicketBookedResponse;
import com.nikhil.ticketflow.tickets.entity.TicketEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketBookedResponse toTicketBookedResponse(TicketEntity entity){
        EventEntity eventEntity = entity.getEvent();
        EventDetailsResponse response = EventDetailsResponse.builder()
                .eventId(eventEntity.getId())
                .title(eventEntity.getTitle())
                .description(eventEntity.getDescription())
                .venue(eventEntity.getVenue())
                .eventDate(eventEntity.getEventDate())
                .build();

        return TicketBookedResponse.builder()
                .ticketId(entity.getId())
                .detailsResponse(response)
                .status(entity.getBookingStatus().name())
                .build();
    }
}
