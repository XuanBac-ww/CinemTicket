package com.example.CinemaTicket.models;

import com.example.CinemaTicket.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "seat_row")
    private String seatRow;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<BookingDetail> bookingDetails;
}