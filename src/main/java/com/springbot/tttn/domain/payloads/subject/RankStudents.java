package com.springbot.tttn.domain.payloads.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RankStudents {
    private Long top;
    private String studentId;
    private String studentName;
    private Double scores;
}
