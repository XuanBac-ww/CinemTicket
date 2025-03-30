package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.ConcessionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcessionOrderRepository extends JpaRepository<ConcessionOrder,Long> {
}
