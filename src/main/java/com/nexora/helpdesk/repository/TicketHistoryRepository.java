package com.nexora.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexora.helpdesk.entity.TicketHistory;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory,Long> {

	List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
