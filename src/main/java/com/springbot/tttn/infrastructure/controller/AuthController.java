package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.payloads.auth.LoginRequest;
import com.springbot.tttn.domain.payloads.auth.RegisterRequest;
import com.springbot.tttn.domain.services.AuthService;
import com.springbot.tttn.domain.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Result> login(@Valid @RequestBody LoginRequest loginRequest) {
        ResponseObject responseObject = authService.login(loginRequest);
        return ResponseEntity.status(responseObject.getStatusCode()).body(responseObject.getResult());
    }

    @PostMapping("/register")
    public ResponseEntity<Result> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ResponseObject responseObject = authService.register(registerRequest);
        return ResponseEntity.status(responseObject.getStatusCode()).body(responseObject.getResult());
    }
}
