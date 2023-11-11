package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.domain.payloads.*;
import com.springbot.tttn.domain.services.StudentScoresService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class StudentScoresController {
    @Autowired
    private StudentScoresService studentScoresService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Result> findAll(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                          @RequestParam(name = "limit") int limit,
                                          @RequestParam(name = "search", defaultValue = "") String search) {
        ResponseObject result = studentScoresService.findAll(offset, limit, search);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Result> updateScores(@RequestParam(name = "studentId") String studentId,
                                              @RequestParam(name = "subjectId") Long subjectId,
                                              @RequestBody UpdateScoreDTO newScore) {
        ResponseObject result = studentScoresService.updateScores(newScore.getScores(), studentId, subjectId);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Result> deleteScores(@RequestBody List<DeleteScoresDTO> deleteScoresDTOList){
        ResponseObject result = studentScoresService.deleteScores(deleteScoresDTOList);
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/pdf/student")
    public ResponseEntity<byte[]> exportPDFScoresOfStudent(@RequestParam(name = "studentId", required = false) String studentId,
                                                    HttpServletResponse response) {
        ResponseObject result = studentScoresService.exportDPFScoresForStudent(studentId, response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/pdf/subject")
    public ResponseEntity<byte[]> exportPDFScoresOfSubject(@RequestParam(name = "subjectId", required = false) Long subjectId,
                                                           HttpServletResponse response) {
        ResponseObject result = studentScoresService.exportPDFScoresOfSubject(subjectId, response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export/excel/subject")
    public ResponseEntity<byte[]> exportExcelScoresOfSubject(@RequestParam(name = "subjectId", required = false) Long subjectId,
                                                         HttpServletResponse response) {

        ResponseObject result = studentScoresService.exportExcelScoreForSubject(subjectId, response);
        ExportResponse exportResponse = (ExportResponse) result.getResult().getData();

        return ResponseEntity.status(result.getStatusCode())
                .headers(exportResponse.getHeaders())
                .contentLength(exportResponse.getPdfContent().length)
                .body(exportResponse.getPdfContent());
    }
}
