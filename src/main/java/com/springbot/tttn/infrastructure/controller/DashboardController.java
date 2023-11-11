package com.springbot.tttn.infrastructure.controller;

import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chart")
    public ResponseEntity<Result> chartAverageScores() {
        ResponseObject result = dashboardService.chartAverageScores();
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/total-details")
    public ResponseEntity<Result> totalDetails() {
        ResponseObject result = dashboardService.totalDetails();
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rank-students")
    public ResponseEntity<Result> rankStudents() {
        ResponseObject result = dashboardService.rankStudents();
        return ResponseEntity.status(result.getStatusCode()).body(result.getResult());
    }
}
