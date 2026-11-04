package com.nexora.helpdesk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.helpdesk.dto.RegisterRequest;
import com.nexora.helpdesk.dto.UserResponse;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  public UserController(UserService userService) {
	  this.userService=userService;
  }
  
  //Register user
  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(
          @RequestBody RegisterRequest request) {

      UserResponse response = userService.registerUser(request);

      return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
  
  //Get User By Id
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {

      User user = userService.getUserById(id);

      return ResponseEntity.ok(user);
  }
  
  //Get User By Email
  @GetMapping("/email/{email}")
  public ResponseEntity<User> getUserByEmail(
          @PathVariable String email) {

      User user = userService.getUserByEmail(email);

      return ResponseEntity.ok(user);
  }
//Get All Users
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {

      List<UserResponse> users = userService.getAllUsers();

      return ResponseEntity.ok(users);
  }
 //Update User
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(
          @PathVariable Long id,
          @RequestBody RegisterRequest request) {

      User updatedUser = userService.updateUser(id, request);

      return ResponseEntity.ok(updatedUser);
  } 
 //@DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable Long id) {

      userService.deleteUser(id);

      return ResponseEntity.ok("User deleted successfully!");
  }
	
}

