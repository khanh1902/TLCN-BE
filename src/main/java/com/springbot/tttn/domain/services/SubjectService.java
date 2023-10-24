package com.springbot.tttn.domain.services;

import com.springbot.tttn.application.entities.Subject;
import com.springbot.tttn.domain.payloads.ResponseObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubjectService {
    ResponseObject findAll(int pageIndex, int pageSize, String search);
    ResponseObject save(Subject subject);
    ResponseObject update(Subject newSubject, Long subjectId);
    ResponseObject delete(List<Long> subjectIds);
    ResponseObject AddSubjectForStudentByClassIds(Long subjectId, List<Long> classId);
    ResponseObject AddSubjectForStudentByStudentIds(Long subjectId, List<String> studentIds);
}
