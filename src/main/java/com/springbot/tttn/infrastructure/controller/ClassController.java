package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.domain.payloads.ExportResponse;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.services.ClassService;
import com.springbot.tttn.domain.services.CommonService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    @Autowired
    private CommonService commonService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Result> findAllClasses(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "search", defaultValue = "") String search) {
        ResponseObject result = classService.findAll(offset, limit, search);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Result> addClass(@Validated @RequestBody Class aClass) {
        ResponseObject result = classService.save(aClass);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Result> updateClass(@Validated @RequestBody Class newClass, @PathVariable Long id) {
        ResponseObject result = classService.update(newClass, id);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Result> deleteClass(@RequestParam(name = "ids") List<Long> ids) {
        ResponseObject result = classService.delete(ids);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/not-yet-register")
    public ResponseEntity<Result> yetRegister(@RequestParam(name = "subjectId") Long subjectId) {
        ResponseObject result = commonService.classesYetRegisterSubject(subjectId);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPDFListStudent(@RequestParam(name = "className", required = false) String className,
                                                    HttpServletResponse response) {
        ResponseObject result = classService.exportPDFClasses(className, response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcelListStudent(@RequestParam(name = "className", required = false) String className,
                                                         HttpServletResponse response) {

        ResponseObject result = classService.exportExcelClasses(className, response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }
}
