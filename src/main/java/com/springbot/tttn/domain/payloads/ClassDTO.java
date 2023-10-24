package com.springbot.tttn.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ClassDTO {
    private Long classId;
    private String className;
    private String schoolYear;
    private Long countStudents;
}
