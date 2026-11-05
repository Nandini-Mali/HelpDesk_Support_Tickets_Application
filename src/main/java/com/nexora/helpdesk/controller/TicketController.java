package com.nexora.helpdesk.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.helpdesk.dto.TicketRequest;
import com.nexora.helpdesk.dto.TicketResponse;
import com.nexora.helpdesk.enums.Priority;
import com.nexora.helpdesk.enums.TicketStatus;
import com.nexora.helpdesk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

	private final TicketService ticketService;
	public TicketController(TicketService ticketService) {
		this.ticketService=ticketService;
	}
//create new ticket
  @PostMapping
  public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request,@RequestParam Long userId){
		TicketResponse response=ticketService.createTicket(request, userId);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}

//Get all tickets with pagination
  @GetMapping
  public ResponseEntity<Page<TicketResponse>> getAllTickets(Pageable pageable,@RequestParam Long userId){
	Page<TicketResponse> tickets=ticketService.getAllTickets(pageable, userId);
	return ResponseEntity.ok(tickets);
}
//Get ticket by ID
  @GetMapping("/{ticketId}")
  public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long ticketId, @RequestParam Long userId) {
    TicketResponse response=ticketService.getTicketById(ticketId, userId);
    return ResponseEntity.ok(response);
}
//Get tickets by user
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<TicketResponse>> getTicketsByUser(@PathVariable Long userId,@RequestParam Long requesterId) {
    List<TicketResponse> tickets=ticketService.getTicketsByUser(userId, requesterId);
    return ResponseEntity.ok(tickets);
}

//Update ticket status
  @PutMapping("/{ticketId}/status")
  public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable Long ticketId,@RequestParam TicketStatus status,@RequestParam Long userId) {
	  TicketResponse response=ticketService.updateTicketStatus(ticketId, status, userId);
      return ResponseEntity.ok(response);
}

//Update ticket priority
  @PutMapping("/{ticketId}/priority")
  public ResponseEntity<TicketResponse> updateTicketPriority(@PathVariable Long ticketId,@RequestParam Priority priority,@RequestParam Long userId) {
     TicketResponse response=ticketService.updateTicketPriority(ticketId, priority, userId);
     return ResponseEntity.ok(response);
}

//Filter tickets by status
  @GetMapping("/status/{status}")
  public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable TicketStatus status,@RequestParam Long userId) {
     List<TicketResponse> tickets=ticketService.getTicketsByStatus(status, userId);
     return ResponseEntity.ok(tickets);
}

//Filter tickets by priority
  @GetMapping("/priority/{priority}")
  public ResponseEntity<List<TicketResponse>> getTicketsByPriority(@PathVariable Priority priority,@RequestParam Long userId) {
    List<TicketResponse> tickets =ticketService.getTicketsByPriority(priority, userId);
    return ResponseEntity.ok(tickets);
}

}
