package com.nexora.helpdesk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.helpdesk.dto.TicketHistoryResponse;
import com.nexora.helpdesk.service.TicketHistoryService;
@RestController
@RequestMapping("/api/ticket-history")
public class TicketHistoryController {
	private final TicketHistoryService ticketHistoryService;

    public TicketHistoryController(TicketHistoryService ticketHistoryService) {
    this.ticketHistoryService = ticketHistoryService;
    }
 // Get all history by Ticket ID
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketHistoryResponse>>
                getHistoryByTicketId(@PathVariable Long ticketId,@RequestParam Long userId) {
        List<TicketHistoryResponse> history =ticketHistoryService.getHistoryByTicketId(ticketId, userId);

        return ResponseEntity.ok(history);
    }

// Get particular history by ID
    @GetMapping("/{historyId}")
    public ResponseEntity<TicketHistoryResponse>
            getHistoryById(@PathVariable Long historyId,@RequestParam Long userId) {

        TicketHistoryResponse history =ticketHistoryService.getHistoryById(historyId, userId);
        return ResponseEntity.ok(history);
    }
}
