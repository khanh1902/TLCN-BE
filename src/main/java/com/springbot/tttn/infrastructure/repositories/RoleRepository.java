package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.Role;
import com.springbot.tttn.application.enums.ERole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Transactional
    @Query("SELECT r FROM Role r")
    List<Role> findAll();

    @Transactional
    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    Role findByName(ERole roleName);
}
