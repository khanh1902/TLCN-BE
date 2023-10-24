package com.springbot.tttn.application.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StudentScoresKey implements Serializable {
    @Column(name = "MASV")
    private String studentId;

    @Column(name = "MAMH")
    private Long subjectId;
}