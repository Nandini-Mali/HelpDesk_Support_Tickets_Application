package com.nexora.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.helpdesk.entity.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory,Long> {

}
