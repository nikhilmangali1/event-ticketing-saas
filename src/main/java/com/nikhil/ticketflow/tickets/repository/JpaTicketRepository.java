package com.nikhil.ticketflow.tickets.repository;

import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.tickets.entity.TicketEntity;
import com.nikhil.ticketflow.tickets.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTicketRepository extends JpaRepository<TicketEntity, UUID> {
    List<TicketEntity> findAllByUserId(UUID userId);
    boolean existsByUserIdAndEventIdAndBookingStatus(UUID userId, UUID eventId, BookingStatus bookingStatus);
    Optional<TicketEntity> findByUserIdAndEventIdAndBookingStatus(UUID userId, UUID eventId, BookingStatus bookingStatus);
}
