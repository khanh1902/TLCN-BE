package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.Class;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.ClassDTO(c.classId, c.className, c.schoolYear, COUNT(s)) " +
            "FROM Class c " +
            "LEFT JOIN Student s ON (s.class_.classId = c.classId) " +
            "GROUP BY c.classId, c.className, c.schoolYear " +
            "ORDER BY c.className asc")
    Page<Class> findAll(Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.ClassDTO(c.classId, c.className, c.schoolYear, COUNT(s)) " +
            "FROM Class c " +
            "LEFT JOIN Student s ON (s.class_.classId = c.classId) " +
            "WHERE LOWER(c.className) LIKE LOWER(CONCAT('%', :className, '%'))" +
            "GROUP BY c.classId, c.className, c.schoolYear " +
            "ORDER BY c.className asc")
    Page<Class> findAllByClassName(String className, Pageable pageable);

    @Transactional
    @Query("SELECT c FROM Class c ORDER BY c.className asc")
    List<Class> listAllClass();

    @Transactional
    @Query("SELECT c FROM Class c WHERE c.classId = :classId")
    Class findByClassId(Long classId);

    @Transactional
    @Query("SELECT c FROM Class c WHERE c.className = :className")
    Class findByClassName(String className);

    @Modifying
    @Transactional
    @Query("DELETE FROM Class c WHERE c.classId = :classId")
    void deleteByClassId(Long classId);
}
