package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "LOP")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Class {
    @Id
    @Column(name = "MALP")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(name = "TENLP", unique = true)
    private String className;

    @Column(name = "NK")
    private String schoolYear;

    @JsonIgnore
    @OneToMany(mappedBy = "class_", cascade = CascadeType.REMOVE)
    private List<Student> students;

    public Class(String className, String schoolYear) {
        this.className = className;
        this.schoolYear = schoolYear;
    }

    @Override
    public String toString() {
        return "className='" + this.className + '\'' +
                ", schoolYear='" + this.schoolYear + '\'';
    }
}

