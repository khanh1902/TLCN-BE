package com.springbot.tttn.domain.services;

import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.StudentReq;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface StudentService {
    ResponseObject findAll(int pageIndex, int pageSize, String search);
    ResponseObject findById(String studentId);
    ResponseObject save(StudentReq student);
    ResponseObject update(StudentReq newStudent, String studentId);

    ResponseObject delete(List<String> studentId);
    ResponseObject exportPDFListStudent(HttpServletResponse response);
    ResponseObject exportExcelListStudent(HttpServletResponse response);
}
