package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.application.entities.Subject;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.payloads.StudentDTO;
import com.springbot.tttn.domain.services.CommonService;
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
public class CommonServiceImpl implements CommonService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentScoresRepository studentScoresRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public ResponseObject classesYetRegisterSubject(Long subjectId) {
        Subject isSubject = subjectRepository.findBySubjectId(subjectId);
        if (isSubject == null) {
            return new ResponseObject(HttpStatus.NOT_FOUND, new Result("Subject does not exists", null));
        }

        List<Class> result = new ArrayList<>(); // result classes yet register subject

        List<Class> allClass = classRepository.listAllClass();

        for (Class _class : allClass) {
            List<Student> getStudentByClassId = studentRepository.findByClassId(_class.getClassId());
            boolean isRegister = false;
            for (Student student : getStudentByClassId) {
                StudentScores isStudentScores = studentScoresRepository.findByStudentIdAndSubjectId(student.getStudentId(), subjectId);
                if (isStudentScores != null) {
                    isRegister = true;
                }
            }

            if(!isRegister) result.add(_class);
        }

        return new ResponseObject(HttpStatus.OK, new Result("Get classes successfully", result));
    }

    @Override
    public ResponseObject studentsYetRegisterSubject(Long subjectId) {
        Subject isSubject = subjectRepository.findBySubjectId(subjectId);
        if (isSubject == null) {
            return new ResponseObject(HttpStatus.NOT_FOUND, new Result("Subject does not exists", null));
        }

        List<StudentDTO> result = new ArrayList<>(); // result students yet register subject

        List<StudentDTO> allStudent = studentRepository.listAllStudent();

        for(StudentDTO student : allStudent){
            StudentScores isStudentScore = studentScoresRepository.findByStudentIdAndSubjectId(student.getStudentId(), subjectId);

            if(isStudentScore == null) {
                result.add(student);
            }
        }

        return new ResponseObject(HttpStatus.OK, new Result("Get students successfully", result));
    }
}