package com.example.CinemaTicket.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Long actorId;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String biography;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String nationality;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL)
    private Set<MovieActor> movieActors = new HashSet<>();
}
