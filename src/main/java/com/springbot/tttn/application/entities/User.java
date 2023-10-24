package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "qtv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "maqtv")
    private UUID userId;

    @Column(name = "tendangnhap", nullable = false, unique = true)
    private String username;

    @Column(name = "tenqtv")
    private String fullName;

    @Column(name = "matkhau")
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "khoataikhoan")
    private Boolean isActive;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "qtv_quyentruycap", joinColumns = @JoinColumn(name = "maqtv"),
            inverseJoinColumns = @JoinColumn(name = "maquyen"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Student student;

    public User(UUID userId, String username, String fullName, String password, String email, Set<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.isActive = true;
    }
}
