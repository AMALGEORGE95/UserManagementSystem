package com.demo.user_management_system.controller;
import com.demo.user_management_system.dto.requestDTO.UserRequestDTO;
import com.demo.user_management_system.dto.responseDTO.UserResponseDTO;
import com.demo.user_management_system.enums.Role;
import com.demo.user_management_system.model.User;
import com.demo.user_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "basicAuth")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Operation(summary = "Get all users", description = "Fetches a list of all users that are not deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all users",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No users found",
                    content = @Content(mediaType = "application/json"))
    })

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "Soft delete user by email", description = "Soft deletes a user by setting the 'isDeleted' flag to true based on the user's email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the provided email",
                    content = @Content(mediaType = "application/json"))
    })

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        userService.softDeleteUserByEmail(email);
        return ResponseEntity.ok("User deleted successfully");
    }
}

