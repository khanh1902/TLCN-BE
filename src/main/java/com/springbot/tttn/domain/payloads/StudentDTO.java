package com.springbot.tttn.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StudentDTO {
    private String StudentId;
    private String studentName;
    private String studentAddress;
    private String className;
}
