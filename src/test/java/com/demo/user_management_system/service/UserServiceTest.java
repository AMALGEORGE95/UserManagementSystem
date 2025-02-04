package com.demo.user_management_system.service;
import com.demo.user_management_system.controller.UserRegistrationController;
import com.demo.user_management_system.dto.responseDTO.LoginResponse;
import com.demo.user_management_system.dto.responseDTO.UserResponseDTO;
import com.demo.user_management_system.enums.Role;
import com.demo.user_management_system.model.User;
import com.demo.user_management_system.repository.UserRepository;
import com.demo.user_management_system.service.UserService;
import com.demo.user_management_system.util.IpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
public class UserServiceTest {
    @Mock
    private MockMvc mockMvc;
    @Mock
    private UserRepository userRepository;


    @Mock
    private IpUtils ipUtils;  // Mock the IpUtils class

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRegistrationController userController;

    @InjectMocks
    private UserService userService;

    private User user;
    private User existingUser;
    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        user.setRole(Role.USER);

        existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(new BCryptPasswordEncoder().encode("password123"));
        existingUser.setRole(Role.USER);
        existingUser.setDeleted(true);
        // Initialize test data
        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1.setDeleted(false);

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        user2.setDeleted(false);
        existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setName("Test User");
        existingUser.setDeleted(false);
    }
    @Test
    public void testRegisterNewUser() {
        // Arrange: Mock the repository to return empty for the email
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(ipUtils.getPublicIp()).thenReturn("192.168.1.1");
        when(ipUtils.getCountryByIp("192.168.1.1")).thenReturn("USA");

        // Mock the save method to return the same user object that is passed
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the service method
        User savedUser = userService.registerUser(user);

        // Assert: Check that the user was saved and returned
        assertNotNull(savedUser);
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals("USA", savedUser.getCountry());
        assertEquals("192.168.1.1", savedUser.getIpAddress());
        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    public void testRegisterUserEmailAlreadyRegistered() {
        // Mock the repository to return a user (email already exists)
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Call the service method and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        // Arrange: Mock the repository to return a list of users
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAllByIsDeletedFalse()).thenReturn(users);

        // Act: Call the service method
        List<UserResponseDTO> userResponseDTOList = userService.getAllUsers();

        // Assert: Check that the service correctly transforms the entities to DTOs
        assertNotNull(userResponseDTOList);
        assertEquals(2, userResponseDTOList.size());
        assertEquals("user1@example.com", userResponseDTOList.get(0).getEmail());
        assertEquals("User One", userResponseDTOList.get(0).getName());
        assertEquals("user2@example.com", userResponseDTOList.get(1).getEmail());
        assertEquals("User Two", userResponseDTOList.get(1).getName());

        // Verify repository call
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }
    @Test
    public void testGetAllUsersWhenNoUsersFound() {
        // Arrange: Mock the repository to return an empty list
        when(userRepository.findAllByIsDeletedFalse()).thenReturn(Arrays.asList());

        // Act: Call the service method
        List<UserResponseDTO> userResponseDTOList = userService.getAllUsers();

        // Assert: Check that an empty list is returned
        assertNotNull(userResponseDTOList);
        assertTrue(userResponseDTOList.isEmpty());

        // Verify repository call
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }
    @Test
    public void testGetAllUsersWithDeletedUsers() {
        // Arrange: Mock the repository to return a list of users, including deleted ones
        user1.setDeleted(true); // Mark user1 as deleted
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAllByIsDeletedFalse()).thenReturn(Arrays.asList(user2));

        // Act: Call the service method
        List<UserResponseDTO> userResponseDTOList = userService.getAllUsers();

        // Assert: Ensure only non-deleted users are returned
        assertNotNull(userResponseDTOList);
        assertEquals(1, userResponseDTOList.size());
        assertEquals("user2@example.com", userResponseDTOList.get(0).getEmail());
        assertEquals("User Two", userResponseDTOList.get(0).getName());

        // Verify repository call
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }

    @Test
    public void testGetAllUsersThrowsException() {
        // Arrange: Mock the repository to throw an exception
        when(userRepository.findAllByIsDeletedFalse()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert: Call the service method and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("Database error", exception.getMessage());

        // Verify repository call
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }
    @Test
    public void testSoftDeleteUserByEmail() {
        // Arrange: Mock the repository to return the user
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        // Act: Call the service method to soft delete the user
        userService.softDeleteUserByEmail(existingUser.getEmail());

        // Assert: Verify that the user is marked as deleted
        assertTrue(existingUser.isDeleted(true));
        verify(userRepository, times(1)).save(existingUser);  // Ensure that save was called once with updated user
    }

    @Test
    public void testSoftDeleteUserByEmailUserNotFound() {
        // Arrange: Mock the repository to return empty (user not found)
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.empty());

        // Act & Assert: Expect an exception to be thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.softDeleteUserByEmail(existingUser.getEmail());
        });

        assertEquals("User not found with email: test@example.com", exception.getMessage());
        verify(userRepository, times(0)).save(any());  // Ensure save was not called
    }
    @Test
    public void testValidateUserSuccess() {
        // Arrange: Mock the repository to return the user
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        // Act: Call the service method
        LoginResponse loginResponse = userService.validateUser(user.getEmail(), "password123");

        // Assert: Verify the response
        assertTrue(loginResponse.isSuccess());
        assertEquals("Login successful", loginResponse.getMessage());
    }

    @Test
    public void testValidateUserInvalidPassword() {
        // Arrange: Mock the repository to return the user
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        // Act: Call the service method
        LoginResponse loginResponse = userService.validateUser(user.getEmail(), "wrongPassword");

        // Assert: Verify the response
        assertFalse(loginResponse.isSuccess());
        assertEquals("Invalid password", loginResponse.getMessage());
    }

    @Test
    public void testValidateUserNotFound() {
        // Arrange: Mock the repository to return empty (user not found)
        when(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).thenReturn(Optional.empty());

        // Act: Call the service method
        LoginResponse loginResponse = userService.validateUser(user.getEmail(), "password123");

        // Assert: Verify the response
        assertFalse(loginResponse.isSuccess());
        assertEquals("Invalid email or user not found", loginResponse.getMessage());
    }


}


