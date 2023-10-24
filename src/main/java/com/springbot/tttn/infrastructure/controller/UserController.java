package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ResponseEntity<Result> getCurrentUser(@RequestHeader(name = "Authorization", defaultValue = "") String jwt) {
        ResponseObject result = userService.getCurrentUser(jwt);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }
}
