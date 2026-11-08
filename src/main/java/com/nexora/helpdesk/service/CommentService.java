package com.nexora.helpdesk.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.helpdesk.dto.CommentRequest;
import com.nexora.helpdesk.dto.CommentResponse;
import com.nexora.helpdesk.entity.Comment;
import com.nexora.helpdesk.entity.Ticket;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Role;
import com.nexora.helpdesk.exception.ResourceNotFoundException;
import com.nexora.helpdesk.repository.CommentRepository;
import com.nexora.helpdesk.repository.TicketRepository;
import com.nexora.helpdesk.repository.UserRepository;

@Service
@Transactional
public class CommentService {
     private final CommentRepository commentRepository;
     private final TicketRepository ticketRepository;
     private final UserRepository userRepository;
     
	public CommentService(CommentRepository commentRespository,TicketRepository ticketRespository,UserRepository userRespository) {
		this.commentRepository=commentRespository;
		this.ticketRepository=ticketRespository;
		this.userRepository=userRespository;
		
	}

//Add Comment
	public CommentResponse addComment(Long ticketId,Long userId,CommentRequest request) {
		if (request == null ||
            request.getMessage() == null ||request.getMessage().isBlank()) {

            throw new IllegalArgumentException("Comment message is required");
        }

		User user = getActiveUserById(userId);

        Ticket ticket = findTicketById(ticketId);

        checkTicketAccess(ticket, user);

	    Comment comment=new Comment();
	    comment.setMessage(request.getMessage().trim());
	    comment.setTicket(ticket);
	    comment.setAuthor(user);
	   
	    Comment savedComment=commentRepository.save(comment);
	   
	    return convertToResponse(savedComment);
	}

//Get all comments by ticket ID
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTicketId(Long ticketId, Long userId) {

        User user = getActiveUserById(userId);

        Ticket ticket = findTicketById(ticketId);

        checkTicketAccess(ticket, user);

        return commentRepository.findByTicketId(ticketId)
           .stream().map(this::convertToResponse).collect(Collectors.toList());
    }

 //Get comment by ID
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId, Long userId) {

        User user = getActiveUserById(userId);

        Comment comment = findCommentById(commentId);

        checkTicketAccess(comment.getTicket(), user);

        return convertToResponse(comment);
    }

 //Delete comment
    public void deleteComment(Long commentId, Long userId) {

        User user = getActiveUserById(userId);

        Comment comment = findCommentById(commentId);

        if (user.getRole() == Role.USER) {

            if (!comment.getAuthor().getId().equals(user.getId())) {

                throw new AccessDeniedException("You can delete only your own comments");
            }

        } else if (user.getRole() != Role.AGENT) {

            throw new AccessDeniedException("You are not authorized to delete comments");
        }

        commentRepository.delete(comment);
    }

// Find comment by ID
    private Comment findCommentById(Long commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() ->new ResourceNotFoundException("Comment not found with ID: " + commentId));
    }
 // Find ticket by ID
    private Ticket findTicketById(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
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
 // Check ticket access
    private void checkTicketAccess(Ticket ticket, User user) {

        boolean isOwner = ticket.getCreatedBy().getId().equals(user.getId());

        boolean isAgent = user.getRole() == Role.AGENT;

        if (!isOwner && !isAgent) {

            throw new AccessDeniedException("You are not allowed to access this ticket");
        }
    }
//Convert Entity To Response DTO
  private CommentResponse convertToResponse(Comment comment) {

      return new CommentResponse(
              comment.getId(),
              comment.getMessage(),
              comment.getAuthor().getName(),
              comment.getCreatedAt()
      );
  }
}
