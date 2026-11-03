package com.nexora.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.helpdesk.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

}
