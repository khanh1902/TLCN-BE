package com.springbot.tttn.domain.services;

import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.domain.payloads.DeleteScoresDTO;
import com.springbot.tttn.domain.payloads.ResponseObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface StudentScoresService {
    ResponseObject save(StudentScores studentScores);
    ResponseObject updateScores(Double newScores, String studentId, Long subjectId);
    ResponseObject findAll(int pageIndex, int pageSize, String search);

    ResponseObject deleteScores(List<DeleteScoresDTO> deleteScoresDTOList);

    ResponseObject exportDPFScoresForStudent(String studentId, HttpServletResponse response);
    ResponseObject exportPDFScoresOfSubject(Long subjectId, HttpServletResponse response);

    ResponseObject exportExcelScoreForSubject(Long subjectId, HttpServletResponse response);

}
