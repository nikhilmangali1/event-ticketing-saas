package com.nikhil.ticketflow.event.repository;

import com.nikhil.ticketflow.event.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, UUID> {
}
