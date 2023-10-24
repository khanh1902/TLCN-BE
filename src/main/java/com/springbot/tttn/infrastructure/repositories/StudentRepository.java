package com.springbot.tttn.infrastructure.repositories;

import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.domain.payloads.StudentDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO(s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId)" +
            "ORDER BY c.className desc ")
    Page<Student> findAll(Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO(s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId)" +
            "WHERE LOWER(s.studentId) LIKE LOWER(CONCAT('%', :studentId, '%'))" +
            "ORDER BY s.studentId desc")
    Page<Student> findAllByStudentId(String studentId, Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO(s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId)" +
            "WHERE LOWER(s.studentName) LIKE LOWER(CONCAT('%', :studentName, '%'))" +
            "ORDER BY s.studentId desc")
    Page<Student> findAllByStudentName(String studentName, Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO(s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId)" +
            "WHERE LOWER(s.studentAddress) LIKE LOWER(CONCAT('%', :studentAddress, '%'))" +
            "ORDER BY s.studentId desc")
    Page<Student> findAllByStudentAddress(String studentAddress, Pageable pageable);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO(s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId)" +
            "WHERE LOWER(s.class_.className) LIKE LOWER(CONCAT('%', :className, '%'))" +
            "ORDER BY s.studentId desc")
    Page<Student> findAllByClassName(String className, Pageable pageable);
    @Query("SELECT COUNT(s) FROM Student s")
    Long countStudents();

    @Transactional
    @Query("SELECT st FROM Student st WHERE st.studentId = :studentId")
    Student findByStudentId(String studentId);

    @Transactional
    @Query("SELECT st FROM Student st WHERE st.class_.className = :className")
    List<Student> findByClassName(String className);

    @Transactional
    @Query("SELECT st FROM Student st WHERE st.class_.classId = :classId")
    List<Student> findByClassId(Long classId);

    @Transactional
    @Query("SELECT NEW com.springbot.tttn.domain.payloads.StudentDTO (s.studentId, s.studentName, s.studentAddress, c.className) " +
            "FROM Student s " +
            "INNER JOIN Class c ON (c.classId = s.class_.classId) " +
            "ORDER BY s.class_.className asc")
    List<StudentDTO> listAllStudent();

    @Modifying
    @Transactional
    @Query("DELETE FROM Student st WHERE st.studentId = :studentId")
    void deleteByStudentId(String studentId);
}
