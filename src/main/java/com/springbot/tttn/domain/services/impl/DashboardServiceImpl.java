package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.application.entities.Subject;
import com.springbot.tttn.domain.payloads.AverageScores;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.payloads.TotalDetails;
import com.springbot.tttn.domain.payloads.subject.RankStudents;
import com.springbot.tttn.domain.services.DashboardService;
import com.springbot.tttn.infrastructure.repositories.ClassRepository;
import com.springbot.tttn.infrastructure.repositories.StudentRepository;
import com.springbot.tttn.infrastructure.repositories.StudentScoresRepository;
import com.springbot.tttn.infrastructure.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private StudentScoresRepository studentScoresRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public ResponseObject chartAverageScores() {
        List<AverageScores> result = new ArrayList<>();

        List<Subject> findAllSubject = subjectRepository.findAll();

        for (Subject subject : findAllSubject) {
            List<StudentScores> getScoresBySubjectId = studentScoresRepository.findBySubjectId(subject.getSubjectId());

            if(getScoresBySubjectId.isEmpty()) continue;

            double sumScores = 0D;

            int size = 0;

            for (StudentScores scores : getScoresBySubjectId) {
                if (scores.getScores() == null) continue;

                sumScores += scores.getScores();
                size++;

            }

            Double averageScores = sumScores / size * 100 / 100;

            result.add(new AverageScores(subject.getSubjectName(), averageScores));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get average scores successfully", result));
    }

    @Override
    public ResponseObject totalDetails() {
        Long totalStudents = studentRepository.countStudents();
        Long totalClass = classRepository.countClass();
        Long totalSubjects = subjectRepository.countSubject();
        Long totalScores = studentScoresRepository.countScores();
        TotalDetails totalDetails = new TotalDetails(totalStudents, totalClass, totalSubjects, totalScores);
        return new ResponseObject(HttpStatus.OK, new Result("Get successfully", totalDetails));
    }

    @Override
    public ResponseObject rankStudents() {
        List<Student> allStudents = studentRepository.findAll();
        List<RankStudents> rankStudents = new ArrayList<>();
        for(Student student : allStudents) {
            List<StudentScores> findStudentScoresByStudentId = studentScoresRepository.findByStudentId(student.getStudentId());
            if(findStudentScoresByStudentId.isEmpty()) continue;

            Double sumScores = 0D;
            int size = 0;
            for(StudentScores studentScores: findStudentScoresByStudentId) {
                if(studentScores.getScores() == null) continue;
                sumScores+= studentScores.getScores();
                size++;
            }
            Double averageScores = sumScores/size *100/100;
            if(averageScores.isNaN()) continue;
            RankStudents rs = new RankStudents(null, student.getStudentId(), student.getStudentName(), averageScores);
            rankStudents.add(rs);

        }

//        sort result follow scores
        List<RankStudents> result = new ArrayList<>();
        for(int i = 0; i< 3; i++) {
            RankStudents topOne = rankStudents.get(0);
            for (RankStudents rankStudent : rankStudents) {
                if (rankStudent.getScores() > topOne.getScores()) {
                    topOne = rankStudent;
                }
            }
            topOne.setTop((long) i + 1);
            result.add(topOne);
            rankStudents.remove(topOne);
        }

        return new ResponseObject(HttpStatus.OK, new Result("Rank Successfully", result));
    }
}
