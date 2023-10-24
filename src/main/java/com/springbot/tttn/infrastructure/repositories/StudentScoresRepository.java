package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.application.entities.StudentScoresKey;
import com.springbot.tttn.domain.payloads.StudentScoresDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentScoresRepository extends JpaRepository<StudentScores, StudentScoresKey> {

    @Transactional
    @Query("SELECT ss FROM StudentScores ss WHERE ss.id.studentId = :studentId AND ss.id.subjectId = :subjectId")
    StudentScores findByStudentIdAndSubjectId(String studentId, Long subjectId);

    @Transactional
    @Query("SELECT ss FROM StudentScores ss WHERE ss.id.studentId = :studentId AND ss.id.subjectId = :subjectId")
    List<StudentScores> findAllByStudentIdAndSubjectId(String studentId, Long subjectId);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentScoresDTO(ss.id.studentId, st.studentName, ss.id.subjectId, sj.subjectName, cl.className, ss.scores) " +
            "FROM StudentScores ss " +
            "INNER JOIN ss.student st " +
            "INNER JOIN st.class_ cl " +
            "INNER JOIN ss.subject sj " +
            "ORDER BY ss.id.subjectId asc")
    Page<StudentScores> findAll(Pageable pageable);
    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentScoresDTO(ss.id.studentId, st.studentName, ss.id.subjectId, sj.subjectName, cl.className, ss.scores) " +
            "FROM StudentScores ss " +
            "INNER JOIN ss.student st " +
            "INNER JOIN st.class_ cl " +
            "INNER JOIN ss.subject sj " +
            "WHERE LOWER(sj.subjectName) LIKE LOWER(CONCAT('%', :subjectName, '%'))" +
            "ORDER BY ss.id.subjectId asc")
    Page<StudentScores> findAllBySubjectName(String subjectName, Pageable pageable);
    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentScoresDTO(ss.id.studentId, st.studentName, ss.id.subjectId, sj.subjectName, cl.className, ss.scores) " +
            "FROM StudentScores ss " +
            "INNER JOIN ss.student st " +
            "INNER JOIN st.class_ cl " +
            "INNER JOIN ss.subject sj " +
            "WHERE LOWER(ss.id.studentId) LIKE LOWER(CONCAT('%', :studentId, '%'))" +
            "ORDER BY ss.id.subjectId asc")
    Page<StudentScores> findAllByStudentId(String studentId, Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentScoresDTO(ss.id.studentId, st.studentName, ss.id.subjectId, sj.subjectName, cl.className, ss.scores) " +
            "FROM StudentScores ss " +
            "INNER JOIN ss.student st " +
            "INNER JOIN st.class_ cl " +
            "INNER JOIN ss.subject sj " +
            "WHERE LOWER(cl.className) LIKE LOWER(CONCAT('%', :className, '%'))" +
            "ORDER BY ss.id.subjectId asc")
    Page<StudentScores> findAllByClassName(String className, Pageable pageable);

    @Transactional
    @Query("SELECT ss FROM StudentScores ss WHERE ss.id.studentId = :studentId")
    List<StudentScores> findByStudentId(String studentId);

    @Transactional
    @Query("SELECT ss FROM StudentScores ss WHERE ss.id.subjectId = :subjectId")
    List<StudentScores> findBySubjectId(Long subjectId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentScores st WHERE st.id.studentId = :studentId")
    void DeleteByStudentId(String studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentScores st WHERE st.id.studentId = :studentId AND st.id.subjectId = :subjectId")
    void DeleteByStudentIdAndSubjectId(String studentId, Long subjectId);
}
