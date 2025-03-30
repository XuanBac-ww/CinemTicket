package com.example.CinemaTicket.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "applied_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedPromotion {

    // ap dung chuong trinh khuyen mai

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applied_promotion_id")
    private Long appliedPromotionId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;
}