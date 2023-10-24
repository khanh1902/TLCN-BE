package com.springbot.tttn.domain.services;

import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.auth.LoginRequest;
import com.springbot.tttn.domain.payloads.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ResponseObject login (LoginRequest loginRequest);
    ResponseObject register(RegisterRequest registerRequest);
}
