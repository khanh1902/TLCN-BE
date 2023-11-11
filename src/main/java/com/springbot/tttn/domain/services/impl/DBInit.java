package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.Role;
import com.springbot.tttn.application.entities.User;
import com.springbot.tttn.application.enums.ERole;
import com.springbot.tttn.infrastructure.repositories.RoleRepository;
import com.springbot.tttn.infrastructure.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class DBInit {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void generateRole() {
        List<Role> findAll = roleRepository.findAll();
        if(findAll.isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_STUDENT));
        }
    }

    @PostConstruct
    public void createDefaultAdmin () {
        String username = "admin";
        String password = "12345678";
        String fullName = "Van Khanh";
        String email = "admin@ut.edu.vn";

        User isUser = userRepository.findByUserName(username);

        if(isUser == null) {
            Set<Role> setRole = new HashSet<>();

            Role roleExists = roleRepository.findByName(ERole.ROLE_ADMIN);

            setRole.add(roleExists);

            User adminUser = new User(
                    UUID.randomUUID(),
                    username,
                    fullName,
                    passwordEncoder.encode(password),
                    email,
                    setRole
            );

            userRepository.save(adminUser);
        }
    }
}
