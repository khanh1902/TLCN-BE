package com.springbot.tttn.domain.payloads.auth;

import com.springbot.tttn.application.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private boolean isActive;
    private Set<Role> roles;
}
