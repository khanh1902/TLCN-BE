package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "SINHVIEN")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @Column(name = "MASV")
    private String studentId;

    @Column(name = "TENSV", nullable = false)
    private String studentName;

    @Column(name = "DCSV", nullable = false)
    private String studentAddress;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MALP")
    private Class class_;

    @JsonIgnore
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<StudentScores> studentScores;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "maqtv")
    private User user;

    public Student(String studentId, String studentName, String studentAddress, Class class_) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentAddress = studentAddress;
        this.class_ = class_;
    }

    @Override
    public String toString() {
        return "{studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentAddress='" + studentAddress + '\'' +
                ", class_=" + class_.getClassName() + '\'' +
                "}";
    }
}