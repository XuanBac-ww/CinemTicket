package com.example.CinemaTicket.models;

import com.example.CinemaTicket.enums.ScreeningStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "screenings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screening {
    // cac xuat chieu phim

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long screeningId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "screening_type")
    private String screeningType;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    private ScreeningStatus status;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();
}