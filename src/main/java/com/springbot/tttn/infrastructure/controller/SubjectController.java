package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.application.entities.Subject;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.payloads.subject.AddSubjectRequest;
import com.springbot.tttn.domain.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Result> findAllStudent(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "search", defaultValue = "") String search) {
        ResponseObject result = subjectService.findAll(offset, limit, search);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Result> addSubject(@Validated @RequestBody Subject subject) {
        ResponseObject result = subjectService.save(subject);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/add-class")
    public ResponseEntity<Result> addSubjectForStudentByClass(@PathVariable Long id,
                                                              @RequestBody AddSubjectRequest request) {
        ResponseObject result = subjectService.AddSubjectForStudentByClassIds(id, request.getClassId());
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/add-student")
    public ResponseEntity<Result> addSubjectForStudentByStudentIds(@PathVariable Long id,
                                                                   @RequestBody AddSubjectRequest request) {
        ResponseObject result = subjectService.AddSubjectForStudentByStudentIds(id, request.getStudentId());
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateSubject(@Validated @RequestBody Subject newSubject, @PathVariable Long id) {
        ResponseObject result = subjectService.update(newSubject, id);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Result> deleteSubject(@RequestParam List<Long> ids) {
        ResponseObject result = subjectService.delete(ids);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }
}
