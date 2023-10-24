package com.springbot.tttn.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class StudentScoresDTO {
    private String studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private String className;
    private Double scores;
}
