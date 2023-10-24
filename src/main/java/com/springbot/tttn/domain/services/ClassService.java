package com.springbot.tttn.domain.services;

import com.springbot.tttn.application.entities.Class;
import com.springbot.tttn.domain.payloads.ResponseObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassService {
    ResponseObject findAll(int pageIndex, int pageSize, String search);
    ResponseObject save(Class class_);
    ResponseObject update (Class newClass, Long classId);
    ResponseObject delete(List<Long> classIds);
    ResponseObject exportPDFClasses(String className, HttpServletResponse response);
    ResponseObject exportExcelClasses(String className, HttpServletResponse response);

}
