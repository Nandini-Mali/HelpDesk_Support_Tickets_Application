package com.nexora.helpdesk.controller;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.nexora.helpdesk.dto.UpdateUserRequest;
import com.nexora.helpdesk.dto.UserResponse;

import com.nexora.helpdesk.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  public UserController(UserService userService) {
	  this.userService=userService;
  }
//Get User By Id
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

	  UserResponse response = userService.getUserResponseById(id);

      return ResponseEntity.ok(response);
  }
  
//Get User By Email
  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponse> getUserByEmail(
          @PathVariable String email) {

	  UserResponse response  = userService.getUserResponseByEmail(email);

      return ResponseEntity.ok(response);
  }
//Get All Users
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {

      List<UserResponse> users = userService.getAllUsers();

      return ResponseEntity.ok(users);
  }
 //Update User
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
          @PathVariable Long id,
          @Valid @RequestBody UpdateUserRequest request) {

	  UserResponse response = userService.updateUser(id, request);

      return ResponseEntity.ok(response);
  } 
 //Delete user
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
          @PathVariable Long id) {

      userService.deleteUser(id);

      return ResponseEntity.noContent().build();
  }
	
}

