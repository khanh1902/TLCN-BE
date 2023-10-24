package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.User;
import com.springbot.tttn.application.utils.JwtUtils;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.payloads.auth.UserResponse;
import com.springbot.tttn.domain.services.UserService;
import com.springbot.tttn.infrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder encoder;
    @Override
    public ResponseObject getCurrentUser(String jwt) {
        if (jwt.isEmpty()) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, new Result("Missing token", null));
        }
        String username = jwtUtils.getUserNameFromJwt(jwt);

        User userExist = userRepository.findByUserName(username);
        if (userExist == null) {
            return new ResponseObject(HttpStatus.NOT_FOUND, new Result("User does not exists", null));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get current use successfully", new UserResponse(
                userExist.getUserId(),
                userExist.getUsername(),
                userExist.getFullName(),
                userExist.getEmail(),
                userExist.getIsActive(),
                userExist.getRoles()
        )));
    }
}
