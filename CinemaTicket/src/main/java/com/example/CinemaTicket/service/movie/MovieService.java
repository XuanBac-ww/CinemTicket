package com.example.CinemaTicket.service.movie;

import com.example.CinemaTicket.dtos.ImageDto;
import com.example.CinemaTicket.dtos.movie.MovieCreateRequestDto;
import com.example.CinemaTicket.dtos.movie.MovieDto;
import com.example.CinemaTicket.dtos.movie.MovieUpdateRequestDto;
import com.example.CinemaTicket.enums.MovieStatus;
import com.example.CinemaTicket.models.Image;
import com.example.CinemaTicket.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface MovieService {
    // Basic CRUD operations
    Movie getMovieById(Long id);
    Page<MovieDto> getAllMovies(Pageable pageable);
    MovieDto createMovie(MovieCreateRequestDto movieCreateDTO);
    MovieDto updateMovie(Long id, MovieUpdateRequestDto movieUpdateDTO);
    void deleteMovie(Long id);

    // Image handling
    MovieDto addImageToMovie(Long movieId, MultipartFile imageFile, boolean isPoster);
    void removeImageFromMovie(Long movieId, Long imageId);

    // Advanced search & filtering
    Page<MovieDto> searchMovies(String keyword, Pageable pageable);
    Page<MovieDto> filterMoviesByGenre(String genre, Pageable pageable);
    Page<MovieDto> filterMoviesByStatus(MovieStatus status, Pageable pageable);
    Page<MovieDto> filterMoviesByReleaseDate(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<MovieDto> filterMoviesByLanguage(String language, Pageable pageable);
    Page<MovieDto> filterMoviesByAgeRating(String ageRating, Pageable pageable);

    // Movie status management
    MovieDto updateMovieStatus(Long id, MovieStatus status);
    List<MovieDto> getUpcomingMovies(); // Movies with COMING_SOON status
    List<MovieDto> getNowShowingMovies(); // Movies with NOW_SHOWING status

    // Statistics and analytics
    List<MovieDto> getTopRatedMovies(int limit);
    List<MovieDto> getMostViewedMovies(int limit);


    // Dto
    MovieDto mapToDto(Movie movie, Long ticketCount);
    ImageDto mapToDto(Image image);
}
