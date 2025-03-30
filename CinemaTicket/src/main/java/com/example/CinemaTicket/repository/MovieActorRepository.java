package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieActorRepository extends JpaRepository<MovieActor,Long> {
}
