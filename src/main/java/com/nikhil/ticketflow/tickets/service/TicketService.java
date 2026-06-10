package com.nikhil.ticketflow.tickets.service;

import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.event.repository.JpaEventRepository;
import com.nikhil.ticketflow.security.CurrentUser;
import com.nikhil.ticketflow.tickets.dto.response.TicketBookedResponse;
import com.nikhil.ticketflow.tickets.entity.TicketEntity;
import com.nikhil.ticketflow.tickets.enums.BookingStatus;
import com.nikhil.ticketflow.tickets.mapper.TicketMapper;
import com.nikhil.ticketflow.tickets.repository.JpaTicketRepository;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final JpaTicketRepository ticketRepository;
    private final JpaEventRepository eventRepository;
    private final CurrentUser currentUser;
    private final JpaUserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketBookedResponse bookTicket(UUID eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow();

        UUID userId = currentUser.getUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow();

        if(eventEntity.getEventDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Event already completed");
        }

        if(ticketRepository.existsByUserIdAndEventIdAndBookingStatus(userId, eventId,BookingStatus.BOOKED)){
            throw new RuntimeException("you already booked it");
        }

        if(eventEntity.getAvailableSeats() <= 0){
            throw new RuntimeException("No seats available");
        }

        eventEntity.setAvailableSeats(Math.max(eventEntity.getAvailableSeats()-1, 0));
        EventEntity updatedEvent = eventRepository.save(eventEntity);

        TicketEntity ticket = TicketEntity.builder()
                .user(user)
                .event(updatedEvent)
                .bookingStatus(BookingStatus.BOOKED)
                .bookingTime(LocalDateTime.now())
                .build();

        TicketEntity bookedTicket = ticketRepository.save(ticket);
        return ticketMapper.toTicketBookedResponse(bookedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketBookedResponse> getMyTickets() {
        UUID userId = currentUser.getUserId();
        return ticketRepository.findAllByUserId(userId).stream()
                .map(ticketMapper::toTicketBookedResponse)
                .toList();
    }

    @Transactional
    public void cancelTicket(UUID ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow();

        EventEntity eventEntity = ticket.getEvent();

        UUID userId = currentUser.getUserId();

        if(eventEntity.getEventDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Event already completed");
        }

        if(!userId.equals(ticket.getUser().getId())){
            throw new RuntimeException();
        }

        if(ticket.getBookingStatus() != BookingStatus.BOOKED){
            throw new RuntimeException();
        }

        ticket.setBookingStatus(BookingStatus.CANCELLED);
        eventEntity.setAvailableSeats(Math.min(eventEntity.getAvailableSeats()+1, eventEntity.getTotalSeats()));
    }
}
