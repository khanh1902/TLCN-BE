package com.springbot.tttn.domain.services.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.application.entities.Student;
import com.springbot.tttn.application.utils.ExcelUtils;
import com.springbot.tttn.application.utils.FontUtils;
import com.springbot.tttn.application.utils.Helper;
import com.springbot.tttn.application.utils.PdfUtils;
import com.springbot.tttn.domain.payloads.ExportResponse;
import com.springbot.tttn.domain.payloads.ResponseObject;
import com.springbot.tttn.domain.payloads.Result;
import com.springbot.tttn.domain.services.ClassService;
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
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentScoresRepository studentScoresRepository;

    private final Logger logger = LoggerFactory.getLogger(ClassServiceImpl.class);

    @Override
    public ResponseObject findAll(int pageIndex, int pageSize, String search) {
        Pageable pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Class> findAll = classRepository.findAllByClassName(search, pageRequest);
        if (findAll.isEmpty()) {
            findAll = classRepository.findAll(pageRequest);
        }
        return new ResponseObject(HttpStatus.OK, new Result("Get Student successfully", Helper.PageToMap(findAll)));
    }

    @Override
    public ResponseObject save(Class class_) {
        logger.info("Action: Add new class");
        Class isClass = classRepository.findByClassName(class_.getClassName());
        if (isClass != null) {
            logger.info("\t\t\tError: Class already exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Class already exists", null));
        }
        logger.info("\t\t\tCreate new class: " + class_.toString());
        Class createClass = classRepository.save(class_);
        return new ResponseObject(HttpStatus.CREATED, new Result("Save class successfully", createClass));
    }

    @Override
    public ResponseObject update(Class newClass, Long classId) {
        logger.info("Action: Update class");

        Class isClassName = classRepository.findByClassName(newClass.getClassName());
        if (isClassName != null) {
            logger.info("\t\t\tError: Class name" + newClass.getClassName() + "already exists");
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Class name already exists", null));
        }

        Class isClass = classRepository.findByClassId(classId);
        if (isClass == null) {
            logger.info("\t\t\tCreate new class: " + newClass.toString());
            return new ResponseObject(HttpStatus.CREATED, new Result("Save class successfully", classRepository.save(newClass)));
        }


        logger.info("\t\t\tUpdate class " + isClass.toString() + " to " + newClass.toString());
        isClass.setClassName(newClass.getClassName());
        isClass.setSchoolYear(newClass.getSchoolYear());
        return new ResponseObject(HttpStatus.OK, new Result("Update class successfully", classRepository.save(isClass)));
    }

    @Override
    public ResponseObject delete(List<Long> classIds) {
        logger.info("Action: Delete class");

        if (classIds.size() < 2) {
            Class isClass = classRepository.findByClassId(classIds.get(0));
            logger.info("\t\t\tDelete class: " + isClass.toString());
            deleteStudentByClass(isClass.getClassName());
            classRepository.deleteByClassId(classIds.get(0));
            return new ResponseObject(HttpStatus.OK, new Result("Delete class successfully", null));
        }
        boolean isDelete = false;
        for (Long classId : classIds) {
            Class isClass = classRepository.findByClassId(classId);
            if (isClass == null) {
                logger.info("\t\t\tError: Class id " + classId + " does not exists to delete");
                continue;
            }
            isDelete = true;
            logger.info("\t\t\tDelete class: " + isClass.toString());
            deleteStudentByClass(isClass.getClassName());
            classRepository.deleteByClassId(classId);
        }
        if (!isDelete) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, new Result("Error: Classes does not exists to delete", null));
        }
        return new ResponseObject(HttpStatus.OK, new Result("Delete class successfully", null));
    }

    @Override
    public ResponseObject exportPDFClasses(String className, HttpServletResponse response) {
        logger.info("Action: Export file PDF for classes");
        Document document = new Document();
        try {
            Class isClass = classRepository.findByClassName(className);
            if (isClass == null) {
                logger.info("\t\t\tError: Class " + className + " does not exist to export");
                throw new Exception("Class does not exist to export");
            }
            List<Student> allStudentByClass = studentRepository.findByClassId(isClass.getClassId());
            if (allStudentByClass.isEmpty()) {
                logger.info("\t\t\tError: Student in " + className + " is empty");
                throw new Exception("Student is empty to export");
            }
            String downloadFolderPath = System.getProperty("user.home") + File.separator + "Downloads/TTTN";
            String fileName = "DanhSachLop_" + className + ".pdf";
            String filePath = downloadFolderPath + File.separator + fileName;
            File pdfFile = new File(filePath);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // header
            PdfPTable header = PdfUtils.getHeader(FontUtils.timesNewRoman(), FontUtils.timesNewRomanBold());
            PdfPTable typeAndDate = PdfUtils.getTypeAndDate(FontUtils.timesNewRoman(), FontUtils.timesNewRomanItalic());

            // title
            String stringTitle = "DANH SÁCH SINH VIÊN LỚP " + className;

            Paragraph title = new Paragraph(stringTitle, new Font(FontUtils.timesNewRomanBold(), 15));
            title.setSpacingBefore(15);
            title.setAlignment(Element.ALIGN_CENTER);

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
            for (Student student : allStudentByClass) {
                body.addCell(new Paragraph(String.valueOf(++stt), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentId(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentName(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getStudentAddress(), new Font(FontUtils.timesNewRoman(), 10)));
                body.addCell(new Paragraph(student.getClass_().getClassName(), new Font(FontUtils.timesNewRoman(), 10)));
            }
            // detail
            Paragraph detail = new Paragraph("Tổng số sinh viên: " + allStudentByClass.size(), new Font(FontUtils.timesNewRoman(), 10));
            detail.setIndentationLeft(10);

            // sign
            Paragraph sign = new Paragraph("Ký tên", new Font(FontUtils.timesNewRomanBold(), 12));
            sign.setIndentationRight(70);
            sign.setSpacingBefore(15);
            sign.setAlignment(Element.ALIGN_RIGHT);

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
    public ResponseObject exportExcelClasses(String className, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        String fileName = "DanhSachSinhVien.xlsx";
        String filePath = Helper.getFilePath(fileName);
        File excelFile = new File(filePath);

        try {
            Class isClass = classRepository.findByClassName(className);
            if (isClass == null) {
                throw new Exception("Class does not exists to export");
            }

            List<Student> getStudentsByClass = studentRepository.findByClassName(className);
            if (getStudentsByClass.isEmpty()) {
                throw new Exception("Student in class is empty");
            }
            int columnIndexStt = 0;
            int columnIndexStudentId = 1;
            int columnIndexStudentName = 2;
            int columnIndexAddress = 3;

            Sheet sheet = workbook.createSheet(isClass.getClassName());
            int rowIndex = 0;

            Row rowTitle = sheet.createRow(rowIndex);
            //merge colum 1 2 3 to fill title
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 3);
            sheet.addMergedRegion(mergedRegion);
            Cell cellTitle = rowTitle.createCell(columnIndexStt);
            cellTitle.setCellStyle(ExcelUtils.styleHeader(sheet));
            cellTitle.setCellValue("DANH SÁCH SINH VIÊN LỚP " + isClass.getClassName());

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

            rowIndex++;
            int countSTT = 1;
            for (Student student : getStudentsByClass) {
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
            }

            int numberOfColumn = sheet.getRow(1).getPhysicalNumberOfCells();
            for (int columnIndex = 0; columnIndex < numberOfColumn; columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
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


    private void deleteStudentByClass(String className) {
        List<Student> allStudent = studentRepository.findByClassName(className);
        for (Student student : allStudent) {
            studentRepository.deleteByStudentId(student.getStudentId());
        }
    }
}
