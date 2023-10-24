package com.springbot.tttn.domain.payloads.auth;

import com.springbot.tttn.application.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank (message = "Full Name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    private String email;

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setFullName(this.fullName);
        user.setIsActive(true);
        user.setEmail(this.email);
        return user;
    }
}
