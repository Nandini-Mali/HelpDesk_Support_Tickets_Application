package com.nexora.helpdesk.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexora.helpdesk.entity.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

	List<Comment> findByTicketId(Long ticketId);
	
	List<Comment> findByAuthorId(Long authorId);

}
