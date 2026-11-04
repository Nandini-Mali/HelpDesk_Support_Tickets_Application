package com.nexora.helpdesk.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nexora.helpdesk.dto.RegisterRequest;
import com.nexora.helpdesk.dto.UserResponse;
import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.enums.Role;
import com.nexora.helpdesk.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder=passwordEncoder;
    }
    
    //Register new user
    public UserResponse registerUser(RegisterRequest request) {
    	if(userRepository.existsByEmail(request.getEmail())) {
    		throw new RuntimeException("Email already registred");
    	}
    	User user=new User();
    	user.setName(request.getName());
    	user.setEmail(request.getEmail());
    
    
     // Encrypt password
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
    public User updateUser(Long id, RegisterRequest request) {
        User user = getUserById(id);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }
   //Delete user
    public void deleteUser(Long id) {
    	User user=getUserById(id);
    	userRepository.delete(user);
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
