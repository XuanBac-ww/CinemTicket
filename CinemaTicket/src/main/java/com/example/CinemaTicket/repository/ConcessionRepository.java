package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.Concession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcessionRepository extends JpaRepository<Concession,Long> {
}
