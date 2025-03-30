package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening,Long> {
}
