package com.example.CinemaTicket.controller;

import com.example.CinemaTicket.dtos.movie.MovieCreateRequestDto;
import com.example.CinemaTicket.dtos.movie.MovieDto;
import com.example.CinemaTicket.dtos.movie.MovieUpdateRequestDto;
import com.example.CinemaTicket.enums.MovieStatus;
import com.example.CinemaTicket.service.movie.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        log.info("Request to get movie with ID: {}", id);
        return ResponseEntity.ok(movieService.mapToDto(movieService.getMovieById(id), 0L));
    }

    @GetMapping
    public ResponseEntity<Page<MovieDto>> getAllMovies(Pageable pageable) {
        log.info("Request to get all movies, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieCreateRequestDto movieCreateDto) {
        log.info("Request to create a new movie: {}", movieCreateDto.getTitle());
        MovieDto createdMovie = movieService.createMovie(movieCreateDto);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieUpdateRequestDto movieUpdateDto) {
        log.info("Request to update movie with ID: {}", id);
        MovieDto updatedMovie = movieService.updateMovie(id, movieUpdateDto);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("Request to delete movie with ID: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<MovieDto> updateMovieStatus(
            @PathVariable Long id,
            @RequestParam MovieStatus status) {
        log.info("Request to update movie status with ID: {} to status: {}", id, status);
        MovieDto updatedMovie = movieService.updateMovieStatus(id, status);
        return ResponseEntity.ok(updatedMovie);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieDto> addImageToMovie(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "isPoster", defaultValue = "false") boolean isPoster) {
        log.info("Request to add {} to movie with ID: {}", isPoster ? "poster" : "image", id);
        MovieDto updatedMovie = movieService.addImageToMovie(id, imageFile, isPoster);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{movieId}/images/{imageId}")
    public ResponseEntity<Void> removeImageFromMovie(
            @PathVariable Long movieId,
            @PathVariable Long imageId) {
        log.info("Request to remove image with ID: {} from movie with ID: {}", imageId, movieId);
        movieService.removeImageFromMovie(movieId, imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieDto>> searchMovies(
            @RequestParam String keyword,
            Pageable pageable) {
        log.info("Request to search movies with keyword: {}", keyword);
        return ResponseEntity.ok(movieService.searchMovies(keyword, pageable));
    }

    @GetMapping("/filter/genre")
    public ResponseEntity<Page<MovieDto>> filterMoviesByGenre(
            @RequestParam String genre,
            Pageable pageable) {
        log.info("Request to filter movies by genre: {}", genre);
        return ResponseEntity.ok(movieService.filterMoviesByGenre(genre, pageable));
    }

    @GetMapping("/filter/status")
    public ResponseEntity<Page<MovieDto>> filterMoviesByStatus(
            @RequestParam MovieStatus status,
            Pageable pageable) {
        log.info("Request to filter movies by status: {}", status);
        return ResponseEntity.ok(movieService.filterMoviesByStatus(status, pageable));
    }

    @GetMapping("/filter/date")
    public ResponseEntity<Page<MovieDto>> filterMoviesByReleaseDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        log.info("Request to filter movies by release date between {} and {}", startDate, endDate);
        return ResponseEntity.ok(movieService.filterMoviesByReleaseDate(startDate, endDate, pageable));
    }

    @GetMapping("/filter/language")
    public ResponseEntity<Page<MovieDto>> filterMoviesByLanguage(
            @RequestParam String language,
            Pageable pageable) {
        log.info("Request to filter movies by language: {}", language);
        return ResponseEntity.ok(movieService.filterMoviesByLanguage(language, pageable));
    }

    @GetMapping("/filter/age-rating")
    public ResponseEntity<Page<MovieDto>> filterMoviesByAgeRating(
            @RequestParam String ageRating,
            Pageable pageable) {
        log.info("Request to filter movies by age rating: {}", ageRating);
        return ResponseEntity.ok(movieService.filterMoviesByAgeRating(ageRating, pageable));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MovieDto>> getUpcomingMovies() {
        log.info("Request to get upcoming movies");
        return ResponseEntity.ok(movieService.getUpcomingMovies());
    }

    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieDto>> getNowShowingMovies() {
        log.info("Request to get now showing movies");
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieDto>> getTopRatedMovies(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Request to get top {} rated movies", limit);
        return ResponseEntity.ok(movieService.getTopRatedMovies(limit));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<MovieDto>> getMostViewedMovies(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Request to get top {} most viewed movies", limit);
        return ResponseEntity.ok(movieService.getMostViewedMovies(limit));
    }
}
