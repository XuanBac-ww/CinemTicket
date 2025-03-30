package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating,Long> {
}
