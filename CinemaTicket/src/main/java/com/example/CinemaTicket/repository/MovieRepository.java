package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.enums.MovieStatus;
import com.example.CinemaTicket.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query("SELECT m, COALESCE(COUNT(bd.bookingDetailId), 0) as ticketCount FROM Movie m " +
            "LEFT JOIN m.screenings s " +
            "LEFT JOIN Booking b ON b.screening.screeningId = s.screeningId AND " +
            "(b.status = com.example.CinemaTicket.enums.BookingStatus.CONFIRMED OR " +
            "b.status = com.example.CinemaTicket.enums.BookingStatus.COMPLETED) " +
            "LEFT JOIN BookingDetail bd ON bd.booking.bookingId = b.bookingId " +
            "GROUP BY m.movieId " +
            "ORDER BY ticketCount DESC")
    List<Object[]> findMostViewedMoviesByTicketCount(Pageable pageable);

    boolean existsByTitleIgnoreCase(String trim);


    Page<Movie> findByTitleContainingIgnoreCaseOrDirectorContainingIgnoreCase(
            String title, String director, Pageable pageable);

    Page<Movie> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    Page<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Movie> findByLanguageContainingIgnoreCase(String language, Pageable pageable);

    Page<Movie> findByAgeRating(String ageRating, Pageable pageable);

    List<Movie> findByStatusAndReleaseDateAfter(MovieStatus status, LocalDate date);

    List<Movie> findByStatus(MovieStatus status);


    @Query("SELECT m, AVG(r.rating) FROM Movie m LEFT JOIN m.ratings r " +
            "GROUP BY m ORDER BY AVG(r.rating) DESC")
    List<Object[]> findTopRatedMovies(Pageable pageable);
}
