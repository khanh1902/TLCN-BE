package com.springbot.tttn.application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springbot.tttn.application.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quyentruycap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maquyen")
    private Long roleId;

    @Column(name = "tenquyen",unique = true)
    @Enumerated(EnumType.STRING)
    private ERole roleName;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public Role(ERole roleName) {
        this.roleName = roleName;
    }
}
