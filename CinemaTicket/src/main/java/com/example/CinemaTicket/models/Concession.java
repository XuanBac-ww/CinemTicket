package com.example.CinemaTicket.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "concessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concession_id")
    private Long concessionId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String category;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    private String status;

    @OneToMany(mappedBy = "concession", cascade = CascadeType.ALL)
    private Set<ConcessionOrder> concessionOrders = new HashSet<>();
}