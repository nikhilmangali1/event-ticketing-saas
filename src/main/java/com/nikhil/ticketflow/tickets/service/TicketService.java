package com.nikhil.ticketflow.tickets.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import com.nikhil.ticketflow.common.exceptions.ResourceNotFoundException;
import com.nikhil.ticketflow.email.service.EmailService;
import com.nikhil.ticketflow.events.entity.EventEntity;
import com.nikhil.ticketflow.events.repository.JpaEventRepository;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final JpaTicketRepository ticketRepository;
    private final JpaEventRepository eventRepository;
    private final CurrentUser currentUser;
    private final JpaUserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final EmailService emailService;
    private final QRCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @Transactional
    public TicketBookedResponse bookTicket(UUID eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("event not found"));

        UUID userId = currentUser.getUserId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (eventEntity.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event already completed");
        }

        if (ticketRepository.existsByUserIdAndEventIdAndBookingStatus(userId, eventId, BookingStatus.BOOKED)) {
            throw new BadRequestException("you already booked it");
        }

        if (eventEntity.getAvailableSeats() <= 0) {
            throw new BadRequestException("No seats available");
        }

        eventEntity.setAvailableSeats(Math.max(eventEntity.getAvailableSeats() - 1, 0));
        EventEntity updatedEvent = eventRepository.save(eventEntity);

        TicketEntity ticket = TicketEntity.builder()
                .user(user)
                .event(updatedEvent)
                .bookingStatus(BookingStatus.BOOKED)
                .bookingTime(LocalDateTime.now())
                .build();

        TicketEntity bookedTicket = ticketRepository.save(ticket);

        String qrText = qrCodeService.generateQRCodeText(
                bookedTicket.getId(),
                eventEntity.getTitle(),
                eventEntity.getEventDate(),
                eventEntity.getOrganizer().getEmail()
        );
        byte[] qrImage = qrCodeService.generateQRCodeImage(qrText, 300, 300);
        bookedTicket.setQrCodeText(qrText);
        bookedTicket.setQrImage(qrImage);
        ticketRepository.save(bookedTicket);

        emailService.sendTicketBookingEmail(
                user.getEmail(),
                "Yayy! Your ticket is booked",
                "ticket-booked",
                Map.of(
                        "eventName", eventEntity.getTitle(),
                        "venue", eventEntity.getVenue(),
                        "eventDate", eventEntity.getEventDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                        "ticketId", bookedTicket.getId().toString(),
                        "price", "₹" + eventEntity.getPrice(),
                        "organizerEmail", eventEntity.getOrganizer().getEmail()
                ),
                qrImage,
                bookedTicket.getId().toString()
        );
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

        if (eventEntity.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event already completed");
        }

        if (!userId.equals(ticket.getUser().getId())) {
            throw new RuntimeException();
        }

        if (ticket.getBookingStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException();
        }

        ticket.setBookingStatus(BookingStatus.CANCELLED);
        eventEntity.setAvailableSeats(Math.min(eventEntity.getAvailableSeats() + 1, eventEntity.getTotalSeats()));
        emailService.sendHtmlEmail(
                ticket.getUser().getEmail(),
                "Your ticket has been cancelled",
                "ticket-cancelled",
                Map.of(
                        "eventName", eventEntity.getTitle()
                )
        );
    }


    public byte[] getQRCodeImage(UUID ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("ticket not found"));

        UUID userId = currentUser.getUserId();
        if (!userId.equals(ticket.getUser().getId())) {
            throw new BadRequestException("unauthorized");
        }
        return ticket.getQrImage() != null ? ticket.getQrImage() : qrCodeService.generateQRCodeImage(ticket.getQrCodeText(), 300, 300);
    }


    @Transactional
    public Map<String, Object> verifyTicket(UUID eventId, String qrText) {
        UUID ticketId;
        try {
            String decodedJson = new String(Base64.getUrlDecoder().decode(qrText), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(decodedJson);
            ticketId = UUID.fromString(jsonNode.get("id").asText());
        } catch (Exception e) {
            throw new BadRequestException("Invalid QR code format");
        }

        // FInd the ticket
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid ticket"));

        EventEntity event = ticket.getEvent();

        // check if ticket belongs to this event
        if (!event.getId().equals(eventId)) {
            throw new BadRequestException("Ticket does not belong to this event");
        }

        // check if the event is not over
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event has already ended");
        }

        // check if the organizer and verifier is same
        if (!event.getOrganizer().getId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You are not authorized to verify tickets for this event");
        }

        // check the current status of ticket
        if (ticket.getBookingStatus().equals(BookingStatus.CANCELLED)) {
            throw new BadRequestException("Ticket has been cancelled");
        }

        // check if ticket has already been checked-in
        if (ticket.getBookingStatus().equals(BookingStatus.CHECKED_IN)) {
            throw new BadRequestException("Ticket has already been checked in");
        }

        // Check in ticket
        ticket.setBookingStatus(BookingStatus.CHECKED_IN);
        ticketRepository.save(ticket);

        // notify user that he is checked in
        emailService.sendHtmlEmail(
                ticket.getUser().getEmail(),
                "You've successfully checked in",
                "ticket-checked-in",
                Map.of(
                        "userName", ticket.getUser().getName(),
                        "eventName", event.getTitle(),
                        "venue", event.getVenue(),
                        "eventDate", event.getEventDate().format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                        ),
                        "checkInTime", LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                        )
                )
        );

        return Map.of(
                "valid", true,
                "status", BookingStatus.CHECKED_IN.name(),
                "userName", ticket.getUser().getName(),
                "ticketId", ticket.getId().toString(),
                "message", "Ticket verified successfully"
        );
    }
}
