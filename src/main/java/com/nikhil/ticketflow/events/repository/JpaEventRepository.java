package com.nikhil.ticketflow.events.repository;

import com.nikhil.ticketflow.events.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, UUID> {
}
