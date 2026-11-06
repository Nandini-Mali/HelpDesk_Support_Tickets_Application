package com.nexora.helpdesk.service;

import java.time.LocalDateTime;
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
		if (request == null) {
	        throw new IllegalArgumentException("Comment request cannot be null");
	    }
		Ticket ticket=ticketRepository.findById(ticketId)
		  .orElseThrow(()->new ResourceNotFoundException("Ticket not found"));
	
	   User user=userRepository.findById(userId)
			   .orElseThrow(()->new ResourceNotFoundException("User not found"));
	  
	   if (user.getRole() == Role.USER) {
            if (!ticket.getCreatedBy().getId().equals(user.getId())) {
               throw new AccessDeniedException("You can comment only on your own ticket");
           }
      } else if (user.getRole() != Role.AGENT) {
    	  throw new AccessDeniedException("You are not authorized to add comments");
       }

	   
	   Comment comment=new Comment();
	   comment.setMessage(request.getMessage());
	   comment.setTicket(ticket);
	   comment.setAuthor(user);
	   comment.setCreatedAt(LocalDateTime.now());
	   
	   Comment savedComment=commentRepository.save(comment);
	   
	   return convertToResponse(savedComment);
	}

//Get All comments By ticket Id
@Transactional(readOnly = true)
public List<CommentResponse> getCommentsByTicketId(Long ticketId){
	 if (!ticketRepository.existsById(ticketId)) {
         throw new ResourceNotFoundException("Ticket not found");
     }
	 return commentRepository.findByTicketId(ticketId)
		.stream().map(this::convertToResponse).collect(Collectors.toList());
}

//Get Comment By Id
@Transactional(readOnly = true)
  public CommentResponse getCommentById(Long commentId) {
	  Comment comment = commentRepository.findById(commentId)
          .orElseThrow(() ->new ResourceNotFoundException("Comment not found"));

      return convertToResponse(comment);
  }

//Delete Comment
  public void deleteComment(Long commentId,Long userId) {
      Comment comment = commentRepository.findById(commentId)
              .orElseThrow(()->new ResourceNotFoundException("Comment not found"));
      User user=userRepository.findById(userId)
              .orElseThrow(() ->new ResourceNotFoundException("User not found"));
      if (user.getRole()== Role.USER) {
           if (!comment.getAuthor().getId().equals(user.getId())) {
              throw new AccessDeniedException("You can delete only your own comment");
          }
      }else if (user.getRole() != Role.AGENT) {
          throw new AccessDeniedException("You are not authorized to delete comments");
      }
      
      commentRepository.delete(comment);
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
