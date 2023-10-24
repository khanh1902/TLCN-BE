package com.springbot.tttn.domain.payloads.auth;

import com.springbot.tttn.application.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        return user;
    }
}
