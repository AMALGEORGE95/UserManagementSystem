package com.demo.user_management_system.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequestDTO {
    @Schema(example = "John Doe")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(example = "Male")
    @NotNull(message = "Gender is required")
    @Pattern(regexp = "(?i)Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Schema(example = "password123")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // ipAddress and country are excluded from the DTO
}
