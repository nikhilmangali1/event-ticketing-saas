package com.nikhil.ticketflow.events.service;

import com.nikhil.ticketflow.common.service.S3FileService;
import com.nikhil.ticketflow.events.dto.response.EventResponse;
import com.nikhil.ticketflow.events.entity.EventEntity;
import com.nikhil.ticketflow.events.mapper.EventMapper;
import com.nikhil.ticketflow.events.repository.JpaEventRepository;
import com.nikhil.ticketflow.security.CurrentUser;
import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.enums.UserRole;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @InjectMocks
    EventService eventService;

    @Mock
    JpaEventRepository eventRepository;

    @Mock
    EventMapper eventMapper;

    @Mock
    JpaUserRepository userRepository;

    @Mock
    CurrentUser currentUser;

    @Mock
    S3FileService s3FileService;


    @Test
    void getAllEvents_whenEventsSizeIsMoreThanOne_returnsEventResponse() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setName("Nar");
        user.setRole(UserRole.ORGANIZER);
        user.setEmail("nar@mail.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());


        EventEntity event = new EventEntity();
        event.setId(UUID.randomUUID());
        event.setTitle("Test Title");
        event.setDescription("Test description");
        event.setVenue("PG Men");
        event.setEventDate(LocalDateTime.of(2026, 10, 31, 18, 30));
        event.setTotalSeats(15);
        event.setAvailableSeats(15);
        event.setPrice(BigDecimal.valueOf(99.99));
        event.setImageUrl("testUrl");
        event.setOrganizer(user);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toResponse(event, user)).thenReturn(
                EventResponse.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .eventDate(event.getEventDate())
                        .totalSeats(event.getTotalSeats())
                        .availableSeats(event.getAvailableSeats())
                        .price(event.getPrice())
                        .organizerEmail(event.getOrganizer().getEmail())
                        .venue(event.getVenue())
                        .organizerName(event.getOrganizer().getName())
                        .build()
        );
        // Act
        List<EventResponse> responses = eventService.getAllEvents();

        // Assert
        assertEquals(1, responses.size());
        assertEquals(15, responses.getFirst().getTotalSeats());
        verify(eventRepository).findAll();
    }
}
