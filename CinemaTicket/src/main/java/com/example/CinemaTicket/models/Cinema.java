package com.example.CinemaTicket.models;


import com.example.CinemaTicket.enums.CinemasStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cinemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_id")
    private Long cinemaId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CinemasStatus status;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Staff> staff;
}
