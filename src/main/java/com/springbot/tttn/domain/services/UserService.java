package com.springbot.tttn.domain.services;

import com.springbot.tttn.domain.payloads.ResponseObject;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseObject getCurrentUser(String jwt);
}
