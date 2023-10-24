package com.springbot.tttn.domain.services.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.application.entities.Subject;
import com.springbot.tttn.application.utils.FontUtils;
import com.springbot.tttn.application.utils.Helper;
import com.springbot.tttn.application.utils.PdfUtils;
import com.springbot.tttn.domain.payloads.*;
import com.springbot.tttn.domain.services.StudentScoresService;
import com.springbot.tttn.infrastructure.repositories.StudentRepository;
import com.springbot.tttn.infrastructure.repositories.StudentScoresRepository;
import com.springbot.tttn.infrastructure.repositories.SubjectRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StudentScoresServiceImpl implements StudentScoresService {
    @Autowired
    private StudentScoresRepository studentScoresRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Logger logger = LoggerFactory.getLogger(StudentScoresServiceImpl.class);

    @Override
    public ResponseObject save(StudentScores studentScores) {
        logger.info("Action: Add new student scores");
        StudentScores isStudentScores = studentScoresRepository.findByStudentIdAndSubjectId(
                studentScores.getId().getStudentId(),
                studentScores.getId().getSubjectId());

        if (isStudentScores != null) {
            logger.info("\t\t\tError: Student score already exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Student score already exists", null));
        }
        StudentScores createStudentScores = studentScoresRepository.save(studentScores);
        logger.info("\t\t\tNew student score: " + createStudentScores.toString());
        return new ResponseObject(HttpStatus.CREATED, new Result("Add student scores successfully", createStudentScores));
    }

    @Override
    public ResponseObject updateScores(Double newScores, String studentId, Long subjectId) {
        logger.info("Action: Update Scores");
        StudentScores isStudentScores = studentScoresRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        if (isStudentScores == null) {
            logger.info("\t\t\tError: Subject id " + subjectId + " not yet register for student id " + studentId);
            return new ResponseObject(HttpStatus.NOT_FOUND, new Result("Subject not yet register for student", null));
        }
        logger.info("\t\t\tUpdate new scores " + newScores + " the subject " + subjectId + " for student " + studentId + " successfully");
        isStudentScores.setScores(newScores);
        studentScoresRepository.save(isStudentScores);
        return new ResponseObject(HttpStatus.CREATED, new Result("Update scores successfully", isStudentScores));
    }

    @Override
    public ResponseObject findAll(int pageIndex, int pageSize, String search) {
        Pageable pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<StudentScores> findAll = studentScoresRepository.findAllByStudentId(search, pageRequest);
        if (findAll.isEmpty()) {
            findAll = studentScoresRepository.findAllByClassName(search, pageRequest);
        }
        if (findAll.isEmpty()) {
            findAll = studentScoresRepository.findAllBySubjectName(search, pageRequest);
        }
        if (findAll.isEmpty()) {
            findAll = studentScoresRepository.findAll(pageRequest);
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get successfully", Helper.PageToMap(findAll)));
    }

    @Override
    public ResponseObject deleteScores(List<DeleteScoresDTO> studentScoresList) {
        logger.info("Action: Delete Scores");
        if (studentScoresList.size() < 2) {
            logger.info("\t\t\tDelete scores: Student id " + studentScoresList.get(0).getStudentId() + ", subject id " + studentScoresList.get(0).getSubjectId());
            studentScoresRepository.DeleteByStudentIdAndSubjectId(studentScoresList.get(0).getStudentId(), studentScoresList.get(0).getSubjectId());
            return new ResponseObject(HttpStatus.OK, new Result("Delete student successfully", null));
        }
        boolean isDelete = false;
        for (DeleteScoresDTO studentScores : studentScoresList) {
            StudentScores isStudentScores = studentScoresRepository.findByStudentIdAndSubjectId(studentScores.getStudentId(), studentScores.getSubjectId());
            if (isStudentScores == null) {
                logger.info("\t\t\tError: Student id " + studentScores.getStudentId() + " not yet register subject id" + studentScores.getStudentId());
                continue;
            }
            isDelete = true;
            logger.info("\t\t\tDelete scores: Student id " + studentScores.getStudentId() + ", subject id " + studentScores.getSubjectId());
            studentScoresRepository.DeleteByStudentIdAndSubjectId(studentScores.getStudentId(), studentScores.getSubjectId());

        }
        if (!isDelete) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Error: Students scores does not exists to delete", null));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Delete scores successfully", null));
    }

    @Override
    public ResponseObject exportDPFScoresForStudent(String studentId , HttpServletResponse response) {
        logger.info("Action: Export file PDF for scores of student");
        Document document = new Document();

        try {
            Student isStudent = studentRepository.findByStudentId(studentId);
            if (isStudent == null) {
                logger.info("\t\t\tError: Student does not exist to export");
                throw new Exception("Class does not exist to export");
            }

            List<StudentScores> allScoresOfStudent = studentScoresRepository.findByStudentId(studentId);
            if (allScoresOfStudent.isEmpty()) {
                logger.info("\t\t\tError: Scores of student id " + isStudent.getStudentId() + " is empty");
                throw new Exception("Scores of student is empty");
            }

            String fileName = "BangDiem_" + studentId + ".pdf";
            String filePath = Helper.getFilePath(fileName);
            File pdfFile = new File(filePath);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // header
            PdfPTable header = PdfUtils.getHeader(FontUtils.timesNewRoman(), FontUtils.timesNewRomanBold());
            PdfPTable typeAndDate = PdfUtils.getTypeAndDate(FontUtils.timesNewRoman(), FontUtils.timesNewRomanItalic());

            // title
            String stringTitle = "KẾT QUẢ HỌC TẬP VÀ RÈN LUYỆN";
            Paragraph title = new Paragraph(stringTitle, new Font(FontUtils.timesNewRomanBold(), 15));
            title.setSpacingBefore(15);
            title.setAlignment(Element.ALIGN_CENTER);

            Paragraph paragraphStudentName = new Paragraph("Họ và tên: " + isStudent.getStudentName(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphStudentName.setSpacingBefore(15);
            paragraphStudentName.setIndentationLeft(20);

            Paragraph paragraphStudentId = new Paragraph("Mã số sinh viên: " + isStudent.getStudentId(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphStudentId.setIndentationLeft(20);

            Paragraph paragraphAddress = new Paragraph("Nơi sinh: " + isStudent.getStudentAddress(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphAddress.setIndentationLeft(20);

            Paragraph paragraphSchoolYear = new Paragraph("Niên Khóa: " + isStudent.getClass_().getSchoolYear(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphSchoolYear.setIndentationLeft(20);

            PdfPTable body = new PdfPTable(5);
            body.setSpacingBefore(25);
            body.setWidthPercentage(95); // width table = page
            Stream.of("STT", "Mã học phần", "Tên học phần", "Tín chỉ", "Điểm hệ 10")
                    .forEach(columnTitle -> {
                        PdfPCell column = new PdfPCell();
                        column.setPhrase(new Phrase(columnTitle, new Font(FontUtils.timesNewRomanBold(), 10)));
                        body.addCell(column);
                    });
            int stt = 0;
            Long countCredit = 0L;
            double countScoresOfEachSubject = 0D;
            for (StudentScores scores : allScoresOfStudent) {
                if(scores.getScores() == null) continue;
                countCredit += scores.getSubject().getCredit();
                countScoresOfEachSubject += ((double) scores.getSubject().getCredit() * scores.getScores());
                StudentScoresDTO scoresDTO = Helper.parseToStudentScoresDTO(scores);
                body.addCell(new Paragraph(String.valueOf(++stt), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(String.valueOf(scoresDTO.getSubjectId()), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(scoresDTO.getSubjectName(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(String.valueOf(scores.getSubject().getCredit()), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(String.valueOf(scoresDTO.getScores()), new Font(FontUtils.timesNewRoman(), 10)));
            }
            // detail
            Paragraph paragraphCountCredit = new Paragraph("Tổng số tín chỉ tích lũy: " + countCredit, new Font(FontUtils.timesNewRoman(), 10));
            paragraphCountCredit.setSpacingBefore(15);
            paragraphCountCredit.setIndentationLeft(20);

            double averageScores = Math.floor((countScoresOfEachSubject / countCredit) * 100) / 100 ;
            Paragraph paragraphAverageScores = new Paragraph("Điểm trung bình tích lũy: " + averageScores, new Font(FontUtils.timesNewRoman(), 10));
            paragraphAverageScores.setIndentationLeft(20);

            String classification;
            if (averageScores >= 8.5) classification = "Xuất sắc";
            else if (averageScores >= 8.0) classification = "Giỏi";
            else if (averageScores >= 7.0) classification = "Khá";
            else if (averageScores >= 5.0) classification = "Trung Bình";
            else classification = "Yếu";
            Paragraph paragraphClassification = new Paragraph("Xếp loại học lực: " + classification, new Font(FontUtils.timesNewRoman(), 10));
            paragraphClassification.setIndentationLeft(20);

            Paragraph paragraphClassName = new Paragraph("Lớp khóa học: " + isStudent.getClass_().getClassName(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphClassName.setIndentationLeft(20);

            // sign
            Paragraph sign = new Paragraph("Ký tên", new Font(FontUtils.timesNewRomanBold(), 12));
            sign.setIndentationRight(70);
            sign.setSpacingBefore(15);
            sign.setAlignment(Element.ALIGN_RIGHT);

            document.add(header);
            document.add(typeAndDate);
            document.add(title);

            document.add(paragraphStudentName);
            document.add(paragraphStudentId);
            document.add(paragraphAddress);
            document.add(paragraphSchoolYear);
            document.add(body);
            document.add(paragraphCountCredit);
            document.add(paragraphAverageScores);
            document.add(paragraphClassification);
            document.add(paragraphClassName);

            document.add(sign);

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);

            byte[] buffer = new byte[0];
            if (pdfFile.exists()) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setContentLength((int) pdfFile.length());
                try (FileInputStream fis = new FileInputStream(pdfFile); OutputStream os = response.getOutputStream()) {
                    buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }

            return new ResponseObject(HttpStatus.OK, new Result(null, new ExportResponse(headers, buffer)));
        } catch (Exception e) {
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseObject(HttpStatus.NOT_IMPLEMENTED, new Result(null, new ExportResponse(headers, e.getMessage().getBytes())));
        }
    }

    @Override
    public ResponseObject exportPDFScoresOfSubject(Long subjectId, HttpServletResponse response) {
        logger.info("Action: Export file PDF scores of subject");
        Document document = new Document();

        try {
            Subject isSubject = subjectRepository.findBySubjectId(subjectId);
            if (isSubject == null) {
                logger.info("\t\t\tError: Student does not exist to export");
                throw new Exception("Class does not exist to export");
            }

            List<StudentScores> allScoresOfSubject = studentScoresRepository.findBySubjectId(subjectId);
            if (allScoresOfSubject.isEmpty()) {
                logger.info("\t\t\tError: Scores of subject id " + isSubject + " is empty");
                throw new Exception("Scores of subject is empty");
            }

            String fileName = "BangDiem_" + subjectId + ".pdf";
            String filePath = Helper.getFilePath(fileName);
            File pdfFile = new File(filePath);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // header
            PdfPTable header = PdfUtils.getHeader(FontUtils.timesNewRoman(), FontUtils.timesNewRomanBold());
            PdfPTable typeAndDate = PdfUtils.getTypeAndDate(FontUtils.timesNewRoman(), FontUtils.timesNewRomanItalic());

            // title
            String stringTitle = "BẢNG ĐIỂM";
            Paragraph title = new Paragraph(stringTitle, new Font(FontUtils.timesNewRomanBold(), 15));
            title.setSpacingBefore(25);
            title.setAlignment(Element.ALIGN_CENTER);

            Paragraph paragraphSubjectName = new Paragraph("Tên môn học: " + isSubject.getSubjectName(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphSubjectName.setSpacingBefore(15);
            paragraphSubjectName.setIndentationLeft(20);

            Paragraph paragraphCredit = new Paragraph("Số tín chỉ: " + isSubject.getCredit(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphCredit.setIndentationLeft(20);

            PdfPTable body = new PdfPTable(6);
            body.setSpacingBefore(10);
            body.setWidthPercentage(95); // width table = page
            Stream.of("STT", "Mã sinh viên", "Tên sinh viên", "Tên lớp", "Điểm hệ 10", "Xếp loại")
                    .forEach(columnTitle -> {
                        PdfPCell column = new PdfPCell();
                        column.setPhrase(new Phrase(columnTitle, new Font(FontUtils.timesNewRomanBold(), 10)));
                        body.addCell(column);
                    });
            int stt = 0;
            Double sumScoreOfSubject = 0D;
            int countStudent = 0;

            for (StudentScores scores : allScoresOfSubject) {
                if(scores.getScores() == null) continue;
                sumScoreOfSubject += scores.getScores();

                countStudent++;

                StudentScoresDTO scoresDTO = Helper.parseToStudentScoresDTO(scores);

                body.addCell(new Paragraph(String.valueOf(++stt), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(String.valueOf(scoresDTO.getStudentId()), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(scoresDTO.getStudentName(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(scoresDTO.getClassName(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(String.valueOf(scoresDTO.getScores()), new Font(FontUtils.timesNewRoman(), 10)));

                String classification;
                if (scoresDTO.getScores() >= 8.5) classification = "Xuất sắc";
                else if (scoresDTO.getScores() >= 8.0) classification = "Giỏi";
                else if (scoresDTO.getScores() >= 7.0) classification = "Khá";
                else if (scoresDTO.getScores() >= 5.0) classification = "Trung Bình";
                else classification = "Yếu";
                body.addCell(new Paragraph(classification, new Font(FontUtils.timesNewRoman(), 10)));

            }
            // detail
            Paragraph paragraphCountStudent = new Paragraph("Tổng số sinh vên: " + allScoresOfSubject.size(), new Font(FontUtils.timesNewRoman(), 10));
            paragraphCountStudent.setIndentationLeft(20);

            double averageScores = Math.floor((sumScoreOfSubject / countStudent) * 100) / 100 ;
            Paragraph paragraphAverageScores = new Paragraph("Điểm trung bình lớp học phần: " + averageScores, new Font(FontUtils.timesNewRoman(), 10));
            paragraphAverageScores.setIndentationLeft(20);

            // sign
            Paragraph sign = new Paragraph("Ký tên", new Font(FontUtils.timesNewRomanBold(), 12));
            sign.setIndentationRight(70);
            sign.setSpacingBefore(15);
            sign.setAlignment(Element.ALIGN_RIGHT);

            document.add(header);
            document.add(typeAndDate);
            document.add(title);

            document.add(paragraphSubjectName);
            document.add(paragraphCredit);
            document.add(body);
            document.add(paragraphCountStudent);
            document.add(paragraphAverageScores);

            document.add(sign);

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);

            byte[] buffer = new byte[0];
            if (pdfFile.exists()) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setContentLength((int) pdfFile.length());
                try (FileInputStream fis = new FileInputStream(pdfFile); OutputStream os = response.getOutputStream()) {
                    buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }

            return new ResponseObject(HttpStatus.OK, new Result(null, new ExportResponse(headers, buffer)));
        } catch (Exception e) {
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseObject(HttpStatus.NOT_IMPLEMENTED, new Result(null, new ExportResponse(headers, e.getMessage().getBytes())));
        }
    }
}
