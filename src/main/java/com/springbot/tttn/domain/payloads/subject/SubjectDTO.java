package com.springbot.tttn.domain.payloads.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long subjectId;
    private String subjectName;
    private Integer credit;
    private Long countStudents;
}
