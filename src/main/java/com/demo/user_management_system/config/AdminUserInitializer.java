package com.demo.user_management_system.config;

import com.demo.user_management_system.enums.Role;
import com.demo.user_management_system.model.User;
import com.demo.user_management_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));// Hash the password
            admin.setGender("Male");
            admin.setRole(Role.ADMIN);
            admin.setDeleted(false);
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
        }
    }
}
