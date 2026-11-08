package com.nexora.helpdesk.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.helpdesk.dto.TicketHistoryResponse;
import com.nexora.helpdesk.entity.Ticket;
import com.nexora.helpdesk.entity.TicketHistory;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Role;
import com.nexora.helpdesk.exception.ResourceNotFoundException;
import com.nexora.helpdesk.repository.TicketHistoryRepository;
import com.nexora.helpdesk.repository.TicketRepository;
import com.nexora.helpdesk.repository.UserRepository;

@Service
@Transactional
public class TicketHistoryService {
	private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    public TicketHistoryService(TicketHistoryRepository ticketHistoryRepository,TicketRepository ticketRepository,UserRepository userRepository) {
    	this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }
//Get all history by Ticket ID
    @Transactional(readOnly = true)
    public List<TicketHistoryResponse> getHistoryByTicketId(Long ticketId, Long userId) {
    	 User user = getActiveUserById(userId);

         Ticket ticket = findTicketById(ticketId);
        checkHistoryAccess(ticket, user);

        return ticketHistoryRepository.findByTicketIdOrderByChangedAtDesc(ticketId)
                .stream().map(this::convertToResponse).collect(Collectors.toList());
    }
//Get particular history by ID
    @Transactional(readOnly = true)
    public TicketHistoryResponse getHistoryById(Long historyId, Long userId) {
    	 User user = getActiveUserById(userId);

         TicketHistory history = findHistoryById(historyId);

         checkHistoryAccess(history.getTicket(), user);

        return convertToResponse(history);
    }
    
 // Find active user by ID
    private User getActiveUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->new ResourceNotFoundException("User not found with ID: " + userId));

        if (!user.isActive()) {
            throw new ResourceNotFoundException("User is inactive");
        }

        return user;
    }
 // Find ticket by ID
    private Ticket findTicketById(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
    }
 // Find history by ID
    private TicketHistory findHistoryById(Long historyId) {

        return ticketHistoryRepository.findById(historyId)
                .orElseThrow(() ->new ResourceNotFoundException("Ticket history not found with ID: " + historyId));
    }

  
    private void checkHistoryAccess(Ticket ticket, User user) {
     
    	boolean isOwner =ticket.getCreatedBy().getId().equals(user.getId());
        boolean isAgent =user.getRole() == Role.AGENT;
        if (!isOwner && !isAgent) {
            throw new AccessDeniedException("You are not allowed to access this ticket history");
        }
    }
    
 // Convert Entity to Response DTO
    private TicketHistoryResponse convertToResponse(
            TicketHistory history) {

        return new TicketHistoryResponse(
                history.getId(),
                history.getTicket().getId(),
                history.getChangedBy().getName(),
                history.getFieldChanged(),
                history.getOldValue(),
                history.getNewValue(),
                history.getChangedAt()
        );
    }
}


