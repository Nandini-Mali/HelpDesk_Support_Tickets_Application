package com.nexora.helpdesk.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.helpdesk.dto.LoginRequest;
import com.nexora.helpdesk.dto.RegisterRequest;
import com.nexora.helpdesk.dto.UpdateUserRequest;
import com.nexora.helpdesk.dto.UserResponse;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Role;
import com.nexora.helpdesk.exception.DuplicateResourceException;
import com.nexora.helpdesk.exception.InvalidCredentialsException;
import com.nexora.helpdesk.exception.ResourceNotFoundException;
import com.nexora.helpdesk.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder=passwordEncoder;
    }
    
//Register new user
    public UserResponse registerUser(RegisterRequest request) {
    	if (request == null ||
            request.getName() == null || request.getName().isBlank() ||
            request.getEmail() == null ||request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {

            throw new IllegalArgumentException("Name, email and password are required");
        }
    	
    	String email = request.getEmail().trim().toLowerCase();
    	
    	if(userRepository.existsByEmail(email)) {
    		throw new DuplicateResourceException("Email already registered");
    	}
    	User user=new User();
    	user.setName(request.getName().trim());
    	user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
    
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
   }
//User Login
    @Transactional(readOnly = true)
    public UserResponse loginUser(LoginRequest request) {

    	 if (request == null ||
                 request.getEmail() == null || request.getEmail().isBlank() ||
                 request.getPassword() == null || request.getPassword().isBlank()) {

             throw new InvalidCredentialsException("Invalid email or password");
         }
    	 String email = request.getEmail().trim().toLowerCase();
    	 
    	 User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new InvalidCredentialsException("Invalid email or password"));

        if (!user.isActive()||!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
                 throw new InvalidCredentialsException("Invalid email or password");
        }

        return convertToUserResponse(user);
    }
 
//Find the user by Id
@Transactional(readOnly = true)
    public User getUserById(Long  id) {
    	return userRepository.findById(id)
    	  .orElseThrow(()->new ResourceNotFoundException("User not found with Id:"+id));
    }
//Find the user by email
@Transactional(readOnly = true) 
public User getUserByEmail(String email) {

    if (email == null || email.isBlank()) {
        throw new IllegalArgumentException("Email is required");
    }

    String normalizedEmail = email.trim().toLowerCase();

    return userRepository.findByEmail(normalizedEmail)
         .orElseThrow(() ->new ResourceNotFoundException("User not found with email: " + email));
}
    
//Get All users
@Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
    	return userRepository.findAll()
    	 .stream().map(this::convertToUserResponse).collect(Collectors.toList());
     }
    
//Update user details

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
    	if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }
    	
    	if (request.getName() == null ||request.getName().isBlank() ||
            request.getEmail() == null ||request.getEmail().isBlank()) {

            throw new IllegalArgumentException("Name and email are required");
        }
        User user = getUserById(id);
        
        if (!user.isActive()) {
            throw new ResourceNotFoundException("User is inactive");
        }
        String email = request.getEmail().trim().toLowerCase();
        
        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists!");
        }
        user.setName(request.getName().trim());
        user.setEmail(email);

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
//Delete user
    public void deleteUser(Long id) {

        User user = getUserById(id);

        if (!user.isActive()) {
            throw new ResourceNotFoundException("User is already inactive");
        }

        user.setActive(false);

        userRepository.save(user);
    }
   
//Get user response by Id 
@Transactional(readOnly = true)
   public UserResponse getUserResponseById(Long id) {
        User user = getUserById(id);
        if (!user.isActive()) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        return convertToUserResponse(user);
    }
//Get user response by email
 @Transactional(readOnly = true)
 public UserResponse getUserResponseByEmail(String email) {
        User user = getUserByEmail(email);
        return convertToUserResponse(user);
    }
    
//Convert Entity to DTO    
    private UserResponse convertToUserResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
	}

}
