package com.springbot.tttn.domain.payloads.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class StudentAccountResponse {
    private UUID userId;
    private String userName;
    private String fullName;
    private String email;
    private String className;
    private Boolean isActive;
}
