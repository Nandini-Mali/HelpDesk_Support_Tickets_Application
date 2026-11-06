package com.nexora.helpdesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexora.helpdesk.entity.Comment;
import com.nexora.helpdesk.entity.Ticket;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

	List<Comment> findByTicketId(Long ticketId);
	
	List<Comment> findByAuthorId(Long ticketId);

}
