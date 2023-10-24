package com.springbot.tttn.domain.services.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.application.entities.StudentScores;
import com.springbot.tttn.application.utils.ExcelUtils;
import com.springbot.tttn.application.utils.FontUtils;
import com.springbot.tttn.application.utils.Helper;
import com.springbot.tttn.application.utils.PdfUtils;
import com.springbot.tttn.domain.payloads.*;
import com.springbot.tttn.domain.services.StudentService;
import com.springbot.tttn.infrastructure.repositories.ClassRepository;
import com.springbot.tttn.infrastructure.repositories.StudentRepository;
import com.springbot.tttn.infrastructure.repositories.StudentScoresRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentScoresRepository studentScoresRepository;

    private Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Override
    public ResponseObject findAll(int pageIndex, int pageSize, String search) {
        Pageable pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Student> findAll = studentRepository.findAllByStudentId(search, pageRequest);
        if (findAll.isEmpty()) {
            findAll = studentRepository.findAllByStudentName(search, pageRequest);
        }
        if (findAll.isEmpty()) {
            findAll = studentRepository.findAllByStudentAddress(search, pageRequest);
        }
        if (findAll.isEmpty()) {
            findAll = studentRepository.findAllByClassName(search, pageRequest);
        }
        if (findAll.isEmpty()) {
            findAll = studentRepository.findAll(pageRequest);
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get Student successfully", Helper.PageToMap(findAll)));
    }

    @Override
    public ResponseObject findById(String studentId) {
        logger.info("Action: Find student by Id");
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            logger.info("\t\t\tStudent does not exists");
            return new ResponseObject(HttpStatus.NOT_FOUND, new Result("Student does not exists", null));
        }
        logger.info("\t\t\tStudent: " + student.toString());
        return new ResponseObject(HttpStatus.OK, new Result("Get Student successfully", student));
    }

    @Override
    public ResponseObject save(StudentReq student) {
        logger.info("Action: Create new student");
        Class isClass = classRepository.findByClassName(student.getClassName());
        if (isClass == null) {
            logger.info("\t\t\tError: Class " + student.getClassName() + " does not exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Class " + student.getClassName() + " does not exists", null));
        }
        // count student to generate studentId
        Long countStudent = studentRepository.countStudents();
        Student createStudent = studentRepository.save(new Student(Helper.generateStudentId(countStudent, isClass.getClassId()), student.getStudentName(), student.getStudentAddress(), isClass));
        logger.info("\t\t\tNew student: " + createStudent.toString());
        return new ResponseObject(HttpStatus.CREATED, new Result("Save student successfully", createStudent));

    }

    @Override
    public ResponseObject update(StudentReq newStudent, String studentId) {
        logger.info("Action: Update Student");
        // check class update exists
        Class isClass = classRepository.findByClassName(newStudent.getClassName());
        if (isClass == null) {
            logger.info("\t\t\tError: Class does not exists to update");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Class does not exists to update", null));
        }
        Student isStudent = studentRepository.findByStudentId(studentId);
        if (isStudent == null) {
            logger.info("\t\t\tAdd new Student: " + newStudent.toString());

            // count student to generate studentId
            Long countStudent = studentRepository.countStudents();

            Student createStudent = studentRepository.save(new Student(Helper.generateStudentId(countStudent, isClass.getClassId()), isStudent.getStudentName(), isStudent.getStudentAddress(), isClass));
            return new ResponseObject(HttpStatus.CREATED, new Result("Save student successfully", studentRepository.save(createStudent)));
        }

        logger.info("\t\t\tUpdate Student from " + isStudent.toString() + " to " + new Student(isStudent.getStudentId(), newStudent.getStudentName(), newStudent.getStudentAddress(), isClass));
        isStudent.setStudentAddress(newStudent.getStudentAddress());
        isStudent.setStudentName(newStudent.getStudentName());
        isStudent.setClass_(isClass);
        return new ResponseObject(HttpStatus.OK, new Result("Update student successfully", studentRepository.save(isStudent)));
    }

    @Override
    public ResponseObject delete(List<String> studentIds) {
        logger.info("Action: Delete Student");
        if (studentIds.size() < 2) {
            Student isStudent = studentRepository.findByStudentId(studentIds.get(0));
            logger.info("\t\t\tDelete student: " + isStudent.toString());
            this.deleteStudentScoresByStudent(isStudent.getStudentId());
            studentRepository.deleteByStudentId(studentIds.get(0));
            return new ResponseObject(HttpStatus.OK, new Result("Delete student successfully", null));
        }
        boolean isDelete = false;
        for (String studentId : studentIds) {
            Student isStudent = studentRepository.findByStudentId(studentId);
            if (isStudent == null) {
                logger.info("\t\t\tError: Student id" + studentId + " does not exists to delete");
                continue;
            }
            isDelete = true;
            this.deleteStudentScoresByStudent(studentId);
            logger.info("\t\t\tDelete student: " + isStudent.toString());
            studentRepository.deleteByStudentId(studentId);
        }
        if (!isDelete) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Error: Students does not exists to delete", null));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Delete student successfully", null));
    }

    @Override
    public ResponseObject exportPDFListStudent(HttpServletResponse response) {
        Document document = new Document();

        String fileName = "DanhSachSinhVien.pdf";
        String filePath = Helper.getFilePath(fileName);
        File pdfFile = new File(filePath);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();


            // header
            PdfPTable header = PdfUtils.getHeader(FontUtils.timesNewRoman(), FontUtils.timesNewRomanBold());
            PdfPTable typeAndDate = PdfUtils.getTypeAndDate(FontUtils.timesNewRoman(), FontUtils.timesNewRomanItalic());

            // title
            Paragraph title = new Paragraph("DANH SÁCH SINH VIÊN", new Font(FontUtils.timesNewRomanBold(), 15));
            title.setSpacingBefore(15);
            title.setAlignment(Element.ALIGN_CENTER);

            // body table
            List<StudentDTO> allStudent = studentRepository.listAllStudent();
            PdfPTable body = new PdfPTable(5);
            body.setSpacingBefore(15);
            body.setWidthPercentage(95); // width table = page
            Stream.of("STT", "MSSV", "Tên Sinh Viên", "Địa chỉ", "Tên lớp")
                    .forEach(columnTitle -> {
                        PdfPCell column = new PdfPCell();
                        column.setPhrase(new Phrase(columnTitle, new Font(FontUtils.timesNewRomanBold(), 10)));
                        body.addCell(column);
                    });
            int stt = 0;
            for (StudentDTO student : allStudent) {
                body.addCell(new Paragraph(String.valueOf(++stt), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentId(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentName(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentAddress(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getClassName(), new Font(FontUtils.timesNewRoman(), 10)));
            }
            // detail
            Paragraph detail = new Paragraph("Tổng số sinh viên: " + allStudent.size(), new Font(FontUtils.timesNewRoman(), 10));
            detail.setIndentationLeft(10);

            // sign
            Paragraph sign = new Paragraph("Ký tên", new Font(FontUtils.timesNewRomanBold(), 12));
            sign.setIndentationRight(70);
            sign.setSpacingBefore(15);
            sign.setAlignment(Element.ALIGN_RIGHT);


            // add paragraph to document
            document.add(header);
            document.add(typeAndDate);
            document.add(title);
            document.add(body);
            document.add(detail);
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
                try (
                        FileInputStream fis = new FileInputStream(pdfFile);
                        OutputStream os = response.getOutputStream()) {
                    buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }

            return new ResponseObject(HttpStatus.OK, new Result("Export Error!", new ExportResponse(headers, buffer)));
        } catch (Exception e) {
            document.close();
            logger.error(e.getMessage());
            return new ResponseObject(HttpStatus.NOT_IMPLEMENTED, new Result("Export failed!", e.getMessage()));
        }
    }

    @Override
    public ResponseObject exportExcelListStudent(HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        String fileName = "DanhSachSinhVien.xlsx";
        String filePath = Helper.getFilePath(fileName);
        File excelFile = new File(filePath);

        try {
            List<Student> allStudents = studentRepository.findAll();
            List<Class> allClasses = classRepository.findAll();
            int columnIndexStt = 0;
            int columnIndexStudentId = 1;
            int columnIndexStudentName = 2;
            int columnIndexAddress = 3;
            int columnIndexClassName = 4;

            for (Class class_ : allClasses) {
                Sheet sheet = workbook.createSheet(class_.getClassName());
                int rowIndex = 0;

                Row rowTitle = sheet.createRow(rowIndex);
                //merge colum 1 2 3 to fill title
                CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 3);
                sheet.addMergedRegion(mergedRegion);
                Cell cellTitle = rowTitle.createCell(columnIndexStt);
                cellTitle.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellTitle.setCellValue("DANH SÁCH SINH VIÊN LỚP " + class_.getClassName());

                rowIndex++;

                Row rowHeader = sheet.createRow(rowIndex);
                Cell cellHeader = rowHeader.createCell(columnIndexStt);
                cellHeader.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellHeader.setCellValue("STT");

                cellHeader = rowHeader.createCell(columnIndexStudentId);
                cellHeader.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellHeader.setCellValue("Mã sinh viên");

                cellHeader = rowHeader.createCell(columnIndexStudentName);
                cellHeader.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellHeader.setCellValue("Tên sinh viên");

                cellHeader = rowHeader.createCell(columnIndexAddress);
                cellHeader.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellHeader.setCellValue("Địa chỉ");

                cellHeader = rowHeader.createCell(columnIndexClassName);
                cellHeader.setCellStyle(ExcelUtils.styleHeader(sheet));
                cellHeader.setCellValue("Tên lớp");

                rowIndex++;
                int countSTT = 1;
                for (Student student : allStudents) {

                    if (student.getClass_().getClassName().equals(class_.getClassName())) {
                        Row rowContent = sheet.createRow(rowIndex++);

                        Cell cellContent = rowContent.createCell(columnIndexStt);
                        cellContent.setCellStyle(ExcelUtils.styleContent(sheet));
                        cellContent.setCellValue(String.valueOf(countSTT++));

                        cellContent = rowContent.createCell(columnIndexStudentId);
                        cellContent.setCellStyle(ExcelUtils.styleContent(sheet));
                        cellContent.setCellValue(student.getStudentId());

                        cellContent = rowContent.createCell(columnIndexStudentName);
                        cellContent.setCellStyle(ExcelUtils.styleContent(sheet));
                        cellContent.setCellValue(student.getStudentName());

                        cellContent = rowContent.createCell(columnIndexAddress);
                        cellContent.setCellStyle(ExcelUtils.styleContent(sheet));
                        cellContent.setCellValue(student.getStudentAddress());

                        cellContent = rowContent.createCell(columnIndexClassName);
                        cellContent.setCellStyle(ExcelUtils.styleContent(sheet));
                        cellContent.setCellValue(student.getClass_().getClassName());
                    }
                }
                int numberOfColumn = sheet.getRow(1).getPhysicalNumberOfCells();
                for (int columnIndex = 0; columnIndex < numberOfColumn; columnIndex++) {
                    sheet.autoSizeColumn(columnIndex);
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            byte[] buffer = byteArrayOutputStream.toByteArray();
            if (excelFile.exists()) {
                response.setContentType("application/vnd.ms-excel");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setContentLength(buffer.length);

                OutputStream os = response.getOutputStream();
                os.write(buffer);
            }

            return new ResponseObject(HttpStatus.OK, new Result("Export Success!", new ExportResponse(headers, buffer)));

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseObject(HttpStatus.NOT_IMPLEMENTED, new Result("Export failed!", e.getMessage()));
        }
    }

    private void deleteStudentScoresByStudent(String studentId) {
        List<StudentScores> getAllStudentScoresByStudentId = studentScoresRepository.findByStudentId(studentId);
        for (StudentScores studentScores : getAllStudentScoresByStudentId) {
            studentScoresRepository.DeleteByStudentId(studentScores.getId().getStudentId());
        }
    }
}
