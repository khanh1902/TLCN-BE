package com.springbot.tttn.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportExcelResponse {
    private HttpHeaders headers;
    private ByteArrayOutputStream excelContent;
}
