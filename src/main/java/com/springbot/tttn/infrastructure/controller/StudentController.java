package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.domain.payloads.*;
import com.springbot.tttn.domain.services.CommonService;
import com.springbot.tttn.domain.services.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private CommonService commonService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Result> findAllStudent(@RequestParam(name = "offset", defaultValue = "0") int pageIndex,
                                                 @RequestParam(name = "limit") int pageSize,
                                                 @RequestParam(name = "search", defaultValue = "") String search) {
        ResponseObject result = studentService.findAll(pageIndex, pageSize, search);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Result> addStudent(@RequestBody StudentReq student) {
        ResponseObject result = studentService.save(student);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateStudent(@RequestBody StudentReq newStudent, @PathVariable String id) {
        ResponseObject result = studentService.update(newStudent, id);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Result> deleteStudents(@RequestParam(name = "ids") List<String> ids) {
        ResponseObject result = studentService.delete(ids);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/not-yet-register")
    public ResponseEntity<Result> yetRegister(@RequestParam(name = "subjectId") Long subjectId) {
        ResponseObject result = commonService.studentsYetRegisterSubject(subjectId);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/pdf/all")
    public ResponseEntity<byte[]> exportPDFListStudent(HttpServletResponse response) {

        ResponseObject result = studentService.exportPDFListStudent(response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/excel/all")
    public ResponseEntity<byte[]> exportExcelListStudent(HttpServletResponse response) {

        ResponseObject result = studentService.exportExcelListStudent(response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }
}
