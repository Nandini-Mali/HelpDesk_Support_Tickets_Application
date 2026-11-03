package com.nexora.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.helpdesk.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {

}
