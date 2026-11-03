package com.nexora.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.helpdesk.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

}
