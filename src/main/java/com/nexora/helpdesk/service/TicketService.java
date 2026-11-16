
package com.nexora.helpdesk.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.helpdesk.dto.TicketRequest;
import com.nexora.helpdesk.dto.TicketResponse;
import com.nexora.helpdesk.entity.Ticket;
import com.nexora.helpdesk.entity.TicketHistory;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Priority;
import com.nexora.helpdesk.enums.Role;
import com.nexora.helpdesk.enums.TicketStatus;
import com.nexora.helpdesk.exception.InvalidStatusTransitionException;
import com.nexora.helpdesk.exception.ResourceNotFoundException;
import com.nexora.helpdesk.repository.TicketHistoryRepository;
import com.nexora.helpdesk.repository.TicketRepository;
import com.nexora.helpdesk.repository.UserRepository;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketHistoryRepository ticketHistoryRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    // Create new ticket
    public TicketResponse createTicket(
            TicketRequest request, String authenticatedEmail) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Ticket request cannot be null");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()
                || request.getDescription() == null
                || request.getDescription().isBlank()
                || request.getCategory() == null
                || request.getCategory().isBlank()
                || request.getPriority() == null) {

            throw new IllegalArgumentException(
                    "Title, description, category and priority are required");
        }

        User user = getUserByEmail(authenticatedEmail);

        Ticket ticket = new Ticket();

        ticket.setTitle(request.getTitle().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setCategory(request.getCategory().trim());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(user);

        Ticket savedTicket = ticketRepository.save(ticket);

        saveHistory(savedTicket, user, "STATUS",
                null, TicketStatus.OPEN.name());

        return convertToTicketResponse(savedTicket);
    }

    // Get all tickets based on role
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(String authenticatedEmail) {

        User user = getUserByEmail(authenticatedEmail);

        List<Ticket> tickets;

        if (user.getRole() == Role.AGENT) {
            tickets = ticketRepository.findAll();
        } else {
            tickets = ticketRepository.findByCreatedBy(user);
        }

        return tickets.stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
    }

    // Get ticket by ID
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(
            Long ticketId, String authenticatedEmail) {

        User user = getUserByEmail(authenticatedEmail);
        Ticket ticket = findTicketById(ticketId);

        checkTicketAccess(ticket, user);

        return convertToTicketResponse(ticket);
    }

    // Get tickets created by a particular user
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByUser(
            Long targetUserId, String authenticatedEmail) {

        User requester = getUserByEmail(authenticatedEmail);

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + targetUserId));

        boolean isAgent = requester.getRole() == Role.AGENT;
        boolean isOwner = requester.getId().equals(targetUserId);

        if (!isAgent && !isOwner) {
            throw new AccessDeniedException(
                    "You cannot access another user's tickets");
        }

        return ticketRepository.findByCreatedBy(targetUser)
                .stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
    }

    // Update ticket status - AGENT only
    public TicketResponse updateTicketStatus(
            Long ticketId,
            TicketStatus status,
            String authenticatedEmail) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status cannot be null");
        }

        User user = getUserByEmail(authenticatedEmail);
        Ticket ticket = findTicketById(ticketId);

        if (user.getRole() != Role.AGENT) {
            throw new AccessDeniedException(
                    "Only AGENT can update ticket status");
        }

        TicketStatus oldStatus = ticket.getStatus();

        validateStatusTransition(oldStatus, status);

        if (oldStatus == status) {
            return convertToTicketResponse(ticket);
        }

        ticket.setStatus(status);

        if (status == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        } else {
            ticket.setClosedAt(null);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        saveHistory(updatedTicket, user,
                "STATUS", oldStatus.name(), status.name());

        return convertToTicketResponse(updatedTicket);
    }

    // Update ticket priority - AGENT only
    public TicketResponse updateTicketPriority(
            Long ticketId,
            Priority priority,
            String authenticatedEmail) {

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Priority cannot be null");
        }

        User user = getUserByEmail(authenticatedEmail);
        Ticket ticket = findTicketById(ticketId);

        if (user.getRole() != Role.AGENT) {
            throw new AccessDeniedException(
                    "Only AGENT can update ticket priority");
        }

        Priority oldPriority = ticket.getPriority();

        if (oldPriority == priority) {
            return convertToTicketResponse(ticket);
        }

        ticket.setPriority(priority);

        Ticket updatedTicket = ticketRepository.save(ticket);

        saveHistory(updatedTicket, user,
                "PRIORITY", oldPriority.name(), priority.name());

        return convertToTicketResponse(updatedTicket);
    }

    // Filter tickets by status
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatus(
            TicketStatus status, String authenticatedEmail) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Ticket status cannot be null");
        }

        User user = getUserByEmail(authenticatedEmail);

        List<Ticket> tickets = ticketRepository.findByStatus(status);

        return filterByAccess(tickets, user)
                .stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
    }

    // Filter tickets by priority
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByPriority(
            Priority priority, String authenticatedEmail) {

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Ticket priority cannot be null");
        }

        User user = getUserByEmail(authenticatedEmail);

        List<Ticket> tickets = ticketRepository.findByPriority(priority);

        return filterByAccess(tickets, user)
                .stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
    }

    // Get all tickets with pagination
    @Transactional(readOnly = true)
    public Page<TicketResponse> getAllTickets(
            Pageable pageable, String authenticatedEmail) {

        User user = getUserByEmail(authenticatedEmail);

        Page<Ticket> tickets;

        if (user.getRole() == Role.AGENT) {
            tickets = ticketRepository.findAll(pageable);
        } else {
            tickets = ticketRepository.findByCreatedBy(user, pageable);
        }

        return tickets.map(this::convertToTicketResponse);
    }

    // Find ticket by ID internally
    private Ticket findTicketById(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found with ID: " + ticketId));
    }

    // Find authenticated user by email
    private User getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email));

        if (!user.isActive()) {
            throw new AccessDeniedException(
                    "User account is inactive");
        }

        return user;
    }

    // Check whether authenticated user can access ticket
    private void checkTicketAccess(Ticket ticket, User user) {

        boolean isOwner = ticket.getCreatedBy()
                .getId().equals(user.getId());

        boolean isAgent = user.getRole() == Role.AGENT;

        if (!isOwner && !isAgent) {
            throw new AccessDeniedException(
                    "You are not allowed to access this ticket");
        }
    }

    // Validate status transitions
    private void validateStatusTransition(
            TicketStatus oldStatus,
            TicketStatus newStatus) {

        if (oldStatus == null || newStatus == null) {
            throw new IllegalArgumentException(
                    "Ticket status cannot be null");
        }

        if (oldStatus == newStatus) {
            return;
        }

        boolean validTransition =
                (oldStatus == TicketStatus.OPEN
                        && newStatus == TicketStatus.IN_PROGRESS)
                || (oldStatus == TicketStatus.IN_PROGRESS
                        && newStatus == TicketStatus.CLOSED);

        if (!validTransition) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition from "
                            + oldStatus + " to " + newStatus);
        }
    }

    // Filter tickets according to user role
    private List<Ticket> filterByAccess(
            List<Ticket> tickets, User user) {

        if (user.getRole() == Role.AGENT) {
            return tickets;
        }

        return tickets.stream()
                .filter(ticket -> ticket.getCreatedBy()
                        .getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    // Save ticket history
    private void saveHistory(
            Ticket ticket,
            User changedBy,
            String fieldChanged,
            String oldValue,
            String newValue) {

        TicketHistory history = new TicketHistory();

        history.setTicket(ticket);
        history.setChangedBy(changedBy);
        history.setFieldChanged(fieldChanged);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedAt(LocalDateTime.now());

        ticketHistoryRepository.save(history);
    }

    // Convert ticket entity into response DTO
    private TicketResponse convertToTicketResponse(Ticket ticket) {

        TicketResponse response = new TicketResponse();

        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setCategory(ticket.getCategory());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setClosedAt(ticket.getClosedAt());
        response.setCreatedByName(ticket.getCreatedBy().getName());

        return response;
    }
}
