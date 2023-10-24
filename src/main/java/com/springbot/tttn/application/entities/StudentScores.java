package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DIEMSV")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentScores {
    @EmbeddedId
    private StudentScoresKey id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "MASV")
    private Student student;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "MAMH")
    private Subject subject;

    @Column(name = "DIEM")
    private Double scores;

    @Override
    public String toString() {
        return "{id=" + id +
                ", scores=" + scores +
                '}';
    }
}