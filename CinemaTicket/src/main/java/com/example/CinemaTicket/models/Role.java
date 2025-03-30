package com.example.CinemaTicket.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Quan hệ nhiều-nhiều với Staff
    @ManyToMany(mappedBy = "roles")
    private Set<Staff> staffs = new HashSet<>();

    // Quan hệ nhiều-nhiều với User
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}