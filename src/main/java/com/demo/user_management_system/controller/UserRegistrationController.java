package com.demo.user_management_system.controller;


import com.demo.user_management_system.dto.requestDTO.UserRequestDTO;
import com.demo.user_management_system.dto.responseDTO.UserResponseDTO;
import com.demo.user_management_system.model.User;
import com.demo.user_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class UserRegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Register a new user", description = "Registers a new user in the system by accepting the user details and returning the created user's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, input validation error",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Email already registered",
                    content = @Content(mediaType = "application/json"))
    })
@PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO, BindingResult result) {
    if (result.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    // Convert DTO to entity
    User user = new User();
    user.setName(userRequestDTO.getName());
    user.setEmail(userRequestDTO.getEmail());
    user.setGender(userRequestDTO.getGender());
    user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

    try {
        // Perform registration
        User savedUser = userService.registerUser(user);

        // Convert saved entity back to DTO
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setGender(savedUser.getGender());
        responseDTO.setIpAddress(savedUser.getIpAddress());
        responseDTO.setCountry(savedUser.getCountry());
        responseDTO.setRole(String.valueOf(savedUser.getRole()));



        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    } catch (RuntimeException e) {
        // Return error message in a structured way
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

}
