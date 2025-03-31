package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.enums.BookingStatus;
import com.example.CinemaTicket.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    boolean existsByScreening_Movie_MovieId(Long movieId);

    boolean existsByScreening_Movie_MovieIdAndStatus(Long movieId, BookingStatus bookingStatus);

    boolean existsByScreening_Movie_MovieIdAndStatusIn(Long movieId, List<BookingStatus> confirmed);
}
