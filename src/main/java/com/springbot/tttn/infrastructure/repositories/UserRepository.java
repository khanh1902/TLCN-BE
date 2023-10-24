package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Transactional
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    User findByUserId(UUID userId);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.auth.StudentAccountResponse(u.userId, u.username, u.fullName, u.email, s.class_.className, u.isActive) " +
            "FROM User u " +
            "INNER JOIN Student s ON (s.studentId = u.username) " +
            "WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    Page<User> findAllByFullName(String fullName, Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.auth.StudentAccountResponse(u.userId, u.username, u.fullName, u.email, s.class_.className, u.isActive) " +
            "FROM User u " +
            "INNER JOIN Student s ON (s.studentId = u.username)")
    Page<User> findAll(Pageable pageable);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUserName(String username);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteById(UUID userId);
}
