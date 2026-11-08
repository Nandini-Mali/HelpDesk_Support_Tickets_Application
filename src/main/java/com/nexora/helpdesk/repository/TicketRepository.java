package com.nexora.helpdesk.repository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nexora.helpdesk.entity.Ticket;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Priority;
import com.nexora.helpdesk.enums.TicketStatus;
@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
	boolean existsByCreatedById(Long userId);
	List<Ticket> findByCreatedBy(User user);
	List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(Priority priority);
    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
}
