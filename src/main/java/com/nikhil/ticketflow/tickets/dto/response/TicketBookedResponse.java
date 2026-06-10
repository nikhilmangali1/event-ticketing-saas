package com.nikhil.ticketflow.tickets.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data @Builder
public class TicketBookedResponse {
    private UUID ticketId;
    private EventDetailsResponse detailsResponse;
    private String status;
}
