package com.demo.user_management_system.service;

import com.demo.user_management_system.dto.requestDTO.UserRequestDTO;
import com.demo.user_management_system.dto.responseDTO.LoginResponse;
import com.demo.user_management_system.dto.responseDTO.UserResponseDTO;
import com.demo.user_management_system.enums.Role;
import com.demo.user_management_system.model.User;
import com.demo.user_management_system.repository.UserRepository;
import com.demo.user_management_system.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IpUtils ipUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Transactional
//    public User registerAdmin(User user) {
//        // Check if the email is already registered
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already registered");
//        }
//
//        // Fetch the user's IP address
//        String ipAddress = ipUtils.getPublicIp();
//        user.setIpAddress(ipAddress);
//
//        // Fetch the country based on the IP address
//        String country = ipUtils.getCountryByIp(ipAddress);
//        user.setCountry(country);
//
//        // Explicitly set role to ADMIN
//        user.setRole(Role.ADMIN);
//
//        // Save the user
//        return userRepository.save(user);
//    }

    @Transactional
    public User registerUser(User user) {
        // Check if the email is already registered
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Fetch the user's IP address
        String ipAddress = ipUtils.getPublicIp();
        user.setIpAddress(ipAddress);

        // Fetch the country based on the IP address
        String country = ipUtils.getCountryByIp(ipAddress);
        user.setCountry(country);

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // Save the user
        return userRepository.save(user);
    }

    public LoginResponse validateUser(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(email); // Check for isDeleted = false

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // ✅ Use passwordEncoder.matches() to check password
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return new LoginResponse(true, "Login successful", user.getId());
            } else {
                return new LoginResponse(false, "Invalid password", null); // Inform the client that the password is incorrect
            }
        }

        return new LoginResponse(false, "Invalid email or user not found", null); // Handle user not found case
    }



    public List<UserResponseDTO> getAllUsers() {
        // Fetch all users from the database where isDeleted is false
        List<User> users = userRepository.findAllByIsDeletedFalse();

        // Map User entities to UserDTO objects
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void softDeleteUserByEmail(String email) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Set the isDeleted flag to true instead of deleting the user
        user.setDeleted(true);

        // Save the user with the updated flag
        userRepository.save(user);
    }


    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setIpAddress(user.getIpAddress());
        userDTO.setCountry(user.getCountry());
        userDTO.setRole(String.valueOf(user.getRole()));
        return userDTO;
    }
}
