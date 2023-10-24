package com.springbot.tttn.domain.services.impl;

import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.application.entities.*;
import com.springbot.tttn.application.utils.Helper;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.services.SubjectService;
import com.springbot.tttn.infrastructure.repositories.ClassRepository;
import com.springbot.tttn.infrastructure.repositories.StudentRepository;
import com.springbot.tttn.infrastructure.repositories.StudentScoresRepository;
import com.springbot.tttn.infrastructure.repositories.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentScoresRepository studentScoresRepository;

    private Logger logger = LoggerFactory.getLogger(SubjectServiceImpl.class);

    @Override
    public ResponseObject findAll(int pageIndex, int pageSize, String search) {
        Pageable pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Subject> findAll = subjectRepository.findAllBySubjectName(search, pageRequest);
        if(findAll.isEmpty()){
            findAll = subjectRepository.findAll(pageRequest);
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get subject successfully", Helper.PageToMap(findAll)));
    }

    @Override
    public ResponseObject save(Subject subject) {
        logger.info("Action: Add new Subject");
        Subject isSubject = subjectRepository.findBySubjectName(subject.getSubjectName());
        if (isSubject != null) {
            logger.info("\t\t\tError: Subject already exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Subject already exists", null));
        }

        Subject createSubject = subjectRepository.save(subject);
        logger.info("\t\t\tNew Subject: " + createSubject.toString());

        return new ResponseObject(HttpStatus.CREATED, new Result("Add subject successfully", createSubject));
    }

    @Override
    public ResponseObject update(Subject newSubject, Long subjectId) {
        logger.info("Action: Add new Subject");
        Subject isSubjectName = subjectRepository.findBySubjectName(newSubject.getSubjectName());
        if (isSubjectName == null) {
            logger.info("\t\t\tError: Subject " + newSubject.getSubjectName() + " does not exists to update");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Subject does not exists to update", null));
        }

        Subject isSubject = subjectRepository.findBySubjectId(subjectId);
        if (isSubject == null) {
            Subject createSubject = subjectRepository.save(newSubject);
            logger.info("\t\t\tNew Subject: " + createSubject.toString());
            return new ResponseObject(HttpStatus.CREATED, new Result("Add subject successfully", createSubject));
        }

        logger.info("\t\t\tUpdate subject from " + isSubject.toString() + " to " + newSubject.toString());
        isSubject.setSubjectName(newSubject.getSubjectName());
        isSubject.setCredit(newSubject.getCredit());

        return new ResponseObject(HttpStatus.OK, new Result("Update subject successfully", subjectRepository.save(isSubject)));
    }

    @Override
    public ResponseObject delete(List<Long> subjectIds) {
        logger.info("Action: Delete subject");
        if (subjectIds.size() == 1) {
            Subject isSubject = subjectRepository.findBySubjectId(subjectIds.get(0));
            logger.info("\t\t\tDelete subject: " + isSubject.toString());
            subjectRepository.delete(isSubject);
            return new ResponseObject(HttpStatus.OK, new Result("Delete subject successfully", null));
        }
        boolean isDelete = false;
        for (Long subjectId : subjectIds) {
            Subject isSubject = subjectRepository.findBySubjectId(subjectId);
            if (isSubject == null) {
                logger.info("\t\t\tError: Subject id " + subjectId + " does not exists to delete");
                continue;
            }
            isDelete = true;
            logger.info("\t\t\tDelete class: " + isSubject.toString() + " successfully");
            subjectRepository.deleteBySubjectId(subjectId);
        }
        if(!isDelete) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Subjects does not exists to delete", null));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Delete subject successfully", null));
    }

    @Override
    public ResponseObject AddSubjectForStudentByClassIds(Long subjectId, List<Long> classIds) {
        logger.info("Action: Add subject for class");
        Subject isSubject = subjectRepository.findBySubjectId(subjectId);
        if(isSubject == null) {
            logger.info("\t\t\tError: Subject Id " + subjectId + " does not exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Subject does not exists to add for class", null));
        }

        List<Long> classesRegistered = new ArrayList<>();
        for(Long classId : classIds) {
            Class isClass = classRepository.findByClassId(classId);
            if (isClass == null) {
                logger.info("\t\t\tError: Class Id " + subjectId + " does not exists");
                return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Class does not exists to add subject for class", null));
            }
            boolean isAdded = false;
            List<Student> allStudentByClassId = studentRepository.findByClassId(classId);
            for (Student student : allStudentByClassId) {
                StudentScores isStudentScore = studentScoresRepository.findByStudentIdAndSubjectId(student.getStudentId(), subjectId);

                if (isStudentScore != null) {
                    logger.info("\t\t\tError: Student id " + student.getStudentId() + " registered for the " + isSubject.getSubjectName() + " course");
                    continue;
                }
                isAdded = true;
                StudentScores studentScores = new StudentScores(
                        new StudentScoresKey(student.getStudentId(), subjectId),
                        student,
                        isSubject,
                        null
                );
                studentScoresRepository.save(studentScores);
            }
            if(!isAdded) {
                logger.info("\t\t\tError: All students class " + isClass.getClassName() + "registered for the subject " + isSubject.getSubjectName() + "course");
            }
            logger.info("\t\t\tClass " + isClass.getClassName() + " registered for the subject " + isSubject.getSubjectName() + " course successfully");
            classesRegistered.add(classId);
        }

        Map<String, String> result = new HashMap<>();
        result.put("subjectId", subjectId.toString());
        result.put("classId", classesRegistered.toString());
        return new ResponseObject(HttpStatus.OK, new Result("Register successfully", result));
    }

    @Override
    public ResponseObject AddSubjectForStudentByStudentIds(Long subjectId, List<String> studentIds) {
        logger.info("Action: Add subject for student by student ids");
        Subject isSubject = subjectRepository.findBySubjectId(subjectId);
        if(isSubject == null) {
            logger.info("\t\t\tError: Subject Id " + subjectId + " does not exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Subject does not exists to add for class", null));
        }

        List<String> studentIsAdd = new ArrayList<>();
        for(String studentId : studentIds) {
            Student isStudent = studentRepository.findByStudentId(studentId);
            if(isStudent == null) {
                logger.info("\t\t\tError: Student " + studentId + " does not exists to add subject " + isSubject.getSubjectName());
                continue;
            }

            StudentScores isStudentScore = studentScoresRepository.findByStudentIdAndSubjectId(studentId, subjectId);

            if(isStudentScore != null) {
                logger.info("\t\t\tError: Student id " + studentId + " registered for the " + isSubject.getSubjectName() + " course");
                continue;
            }

            logger.info("\t\t\tStudent id " + studentId + " register for the " + isSubject.getSubjectName() + " course successfully");
            StudentScores studentScores = new StudentScores(
                    new StudentScoresKey(isStudent.getStudentId(), subjectId),
                    isStudent,
                    isSubject,
                    null
            );

            studentScoresRepository.save(studentScores);
            studentIsAdd.add(studentId);
        }

        Map<String, String> result = new HashMap<>();
        result.put("subjectId", subjectId.toString());
        result.put("studentIds", studentIsAdd.toString());
        return new ResponseObject(HttpStatus.OK, new Result("Register successfully", result));
    }
}
