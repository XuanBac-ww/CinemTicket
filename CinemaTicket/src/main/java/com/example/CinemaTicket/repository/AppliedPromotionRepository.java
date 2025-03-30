package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.AppliedPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedPromotionRepository extends JpaRepository<AppliedPromotion,Long> {
}
