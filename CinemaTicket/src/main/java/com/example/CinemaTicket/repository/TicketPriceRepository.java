package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice,Long> {
}
