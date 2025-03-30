package com.example.CinemaTicket.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ticket_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;

    @Column(name = "screening_type_id")
    private String screeningTypeId;

    @Column(name = "seat_type_id")
    private String seatTypeId;

    @Column(name = "day_type")
    private String dayType;

    @Column(name = "time_slot")
    private String timeSlot;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private String status;

    @OneToMany(mappedBy = "ticketPrice", cascade = CascadeType.ALL)
    private Set<BookingDetail> bookingDetails = new HashSet<>();
}