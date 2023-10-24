package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "MONHOC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAMH")
    private Long subjectId;

    @Column(name = "TENMH", nullable = false, unique = true)
    private String subjectName;

    @Column(name = "SOTC", nullable = false)
    private Integer credit ;

    @JsonIgnore
    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private Set<StudentScores> studentScores;

    public Subject(String subjectName, Integer credit) {
        this.subjectName = subjectName;
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "subjectName='" + subjectName + '\'' +
                ", credit=" + credit;
    }
}