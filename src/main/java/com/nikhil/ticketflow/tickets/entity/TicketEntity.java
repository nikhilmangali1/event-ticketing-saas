package com.nikhil.ticketflow.tickets.entity;

import com.nikhil.ticketflow.event.entity.EventEntity;
import com.nikhil.ticketflow.tickets.enums.BookingStatus;
import com.nikhil.ticketflow.users.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private LocalDateTime bookingTime;
}
