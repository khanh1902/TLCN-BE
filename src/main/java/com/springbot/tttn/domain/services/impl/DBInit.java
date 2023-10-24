package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.Role;
import com.springbot.tttn.application.enums.ERole;
import com.springbot.tttn.infrastructure.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBInit {
    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void generateRole() {
        List<Role> findAll = roleRepository.findAll();
        if(findAll.isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_STUDENT));
        }
    }
}
