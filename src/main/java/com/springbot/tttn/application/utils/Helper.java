package com.springbot.tttn.application.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.domain.payloads.StudentScoresDTO;
import com.springbot.tttn.domain.services.impl.SubjectServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper {
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);
    public static Map PageToMap(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", page.getContent());
        map.put("offset", page.getNumber());
        map.put("limit", page.getSize());
        map.put("count", page.getTotalElements());
        return map;
    }

    public static String generateStudentId(Long countStudent, Long classId){
        SimpleDateFormat formatter = new SimpleDateFormat("yyddMM");
        Date date = new Date();
        return formatter.format(date) + countStudent + classId;
    }

    public static StudentScoresDTO parseToStudentScoresDTO(StudentScores scores) {
        return new StudentScoresDTO(
                scores.getId().getStudentId(),
                scores.getStudent().getStudentName(),
                scores.getId().getSubjectId(),
                scores.getSubject().getSubjectName(),
                scores.getStudent().getClass_().getClassName(),
                scores.getScores()
        );
    }
    public static String getFilePath(String fileName) {
        String downloadFolderPath = System.getProperty("user.home") + File.separator + "Downloads/TTTN";
        return downloadFolderPath + File.separator + fileName;
    }
}
