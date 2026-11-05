package com.nexora.helpdesk.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.helpdesk.dto.RegisterRequest;
import com.nexora.helpdesk.dto.UpdateUserRequest;
import com.nexora.helpdesk.dto.UserResponse;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Role;
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
    	if (request == null) {
    	 throw new IllegalArgumentException("Register request cannot be null");
    	}
    	if(userRepository.existsByEmail(request.getEmail())) {
    		throw new RuntimeException("Email already registered");
    	}
    	User user=new User();
    	user.setName(request.getName());
    	user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
    
  // Default role
     user.setRole(Role.USER);
     User savedUser = userRepository.save(user);
     return convertToUserResponse(savedUser);
   }
  //Find the user by Id
    public User getUserById(Long  id) {
    	return userRepository.findById(id)
    			.orElseThrow(()->
    			new RuntimeException("User not found with Id:"+id));
    }
  //Find the user by email
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                new RuntimeException("User not found with email: " + email));
    }
    
   //Get All users
    public List<UserResponse> getAllUsers() {
    	return userRepository.findAll()
    	 .stream().map(this::convertToUserResponse)
    	 .collect(Collectors.toList());
     }
    
    //Update user details
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Update request cannot be null");
        }
        User user = getUserById(id);
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
   //Delete user
    public void deleteUser(Long id) {
    	User user=getUserById(id);
    	userRepository.delete(user);
    }
   
    
    public UserResponse getUserResponseById(Long id) {
        User user = getUserById(id);
        return convertToUserResponse(user);
    }

    public UserResponse getUserResponseByEmail(String email) {
        User user = getUserByEmail(email);
        return convertToUserResponse(user);
    }
    
    
    private UserResponse convertToUserResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
	}

}
