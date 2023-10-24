package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.Subject;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT NEW com.springbot.tttn.domain.payloads.subject.SubjectDTO(s.subjectId, s.subjectName, s.credit, COUNT(ss)) " +
            "FROM Subject s " +
            "LEFT JOIN StudentScores ss ON (s.subjectId = ss.subject.subjectId) " +
            "GROUP BY s.subjectId, s.subjectName, s.credit " +
            "ORDER BY s.subjectId ASC")
    Page<Subject> findAll(Pageable pageable);

    @Query("SELECT NEW com.springbot.tttn.domain.payloads.subject.SubjectDTO(s.subjectId, s.subjectName, s.credit, COUNT(ss)) " +
            "FROM Subject s " +
            "LEFT JOIN StudentScores ss ON (s.subjectId = ss.subject.subjectId) " +// Assuming there's a relationship between Subject and Student
            "WHERE LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :subjectName, '%')) "+
            "GROUP BY s.subjectId, s.subjectName, s.credit " +
            "ORDER BY s.subjectId ASC")
    Page<Subject> findAllBySubjectName(String subjectName, Pageable pageable);

    @Transactional
    @Query("SELECT s FROM Subject s WHERE s.subjectName = :subjectName")
    Subject findBySubjectName(String subjectName);

    @Transactional
    @Query("SELECT s FROM Subject s WHERE s.subjectId = :subjectId")
    Subject findBySubjectId(Long subjectId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Subject c WHERE c.subjectId = :subjectId")
    void deleteBySubjectId(Long subjectId);
}
