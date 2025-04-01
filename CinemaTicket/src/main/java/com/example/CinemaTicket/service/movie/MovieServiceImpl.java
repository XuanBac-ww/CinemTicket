package com.example.CinemaTicket.service.movie;

import com.example.CinemaTicket.dtos.ImageDto;
import com.example.CinemaTicket.dtos.movie.MovieCreateRequestDto;
import com.example.CinemaTicket.dtos.movie.MovieDto;
import com.example.CinemaTicket.dtos.movie.MovieUpdateRequestDto;
import com.example.CinemaTicket.enums.BookingStatus;
import com.example.CinemaTicket.enums.MovieStatus;
import com.example.CinemaTicket.exceptions.ResourceNotFoundException;
import com.example.CinemaTicket.exceptions.ValidateException;
import com.example.CinemaTicket.models.Image;
import com.example.CinemaTicket.models.Movie;
import com.example.CinemaTicket.repository.BookingRepository;
import com.example.CinemaTicket.repository.ImageReposioty;
import com.example.CinemaTicket.repository.MovieRepository;
import com.example.CinemaTicket.repository.ScreeningRepository;
import com.example.CinemaTicket.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;
    private final ImageService imageService;
    private final ScreeningRepository screeningRepository;
    private final ImageReposioty imageReposioty;

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    @Override
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(pageable);
        return movies.map(movie -> mapToDto(movie,0l));
    }

    @Override
    public MovieDto createMovie(MovieCreateRequestDto movieCreateDTO) {
        boolean movieExists = movieRepository.existsByTitleIgnoreCase(movieCreateDTO.getTitle().trim());
        if (movieExists) {
            throw new ValidateException("Movie with title" +movieCreateDTO.getTitle() + "already exists");
        }
        Movie movie = new Movie();
        movie.setTitle(movieCreateDTO.getTitle());
        movie.setDirector(movieCreateDTO.getDirector());
        movie.setDescription(movieCreateDTO.getDescription());
        movie.setReleaseDate(movieCreateDTO.getReleaseDate());
        movie.setDuration(movieCreateDTO.getDuration());
        movie.setGenre(movieCreateDTO.getGenre());
        movie.setLanguage(movieCreateDTO.getLanguage());
        movie.setAgeRating(movieCreateDTO.getAgeRating());
        movie.setStatus(movieCreateDTO.getStatus());

        log.info("Creating new movie: {}", movieCreateDTO.getTitle());
        Movie savedMovie = movieRepository.save(movie);
        return mapToDto(savedMovie, 0L);
    }

    @Override
    public MovieDto updateMovie(Long id, MovieUpdateRequestDto movieUpdateDTO) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with ID " + id + " not found"));
        if (movieUpdateDTO.getGenre() != null) {
            movie.setGenre(movieUpdateDTO.getGenre());
        }

        if (movieUpdateDTO.getLanguage() != null) {
            movie.setLanguage(movieUpdateDTO.getLanguage());
        }

        if (movieUpdateDTO.getAgeRating() != null) {
            if (!isValidAgeRating(movieUpdateDTO.getAgeRating())) {
                throw new ValidateException("Invalid age rating format");
            }
            movie.setAgeRating(movieUpdateDTO.getAgeRating());
        }

        if (movieUpdateDTO.getStatus() != null) {
            // Kiểm tra luồng chuyển đổi trạng thái trước khi cập nhật
            validateStatusTransition(movie, movieUpdateDTO.getStatus());
            movie.setStatus(movieUpdateDTO.getStatus());
        }

        log.info("Updating movie with ID {}: {}", id, movie.getTitle());
        Movie updatedMovie = movieRepository.save(movie);
        return mapToDto(updatedMovie, 0L);
    }

    private boolean isValidAgeRating(String ageRating) {
        // Giả sử các định dạng hợp lệ là: G, PG, PG-13, R, NC-17
        return ageRating != null &&
                (ageRating.equals("G") || ageRating.equals("PG") ||
                        ageRating.equals("PG-13") || ageRating.equals("R") ||
                        ageRating.equals("NC-17"));
    }

    // Phương thức kiểm tra tính hợp lệ của việc chuyển đổi trạng thái
    private void validateStatusTransition(Movie movie, MovieStatus newStatus) {
        if (movie.getStatus() == newStatus) {
            return; // Không có thay đổi trạng thái
        }

        switch (movie.getStatus()) {
            case COMING_SOON:
                if (newStatus == MovieStatus.NOW_SHOWING) {
                    // Kiểm tra ngày phát hành
                    if (movie.getReleaseDate().isAfter(LocalDate.now())) {
                        throw new ValidateException("Cannot change status to NOW_SHOWING before release date");
                    }

                    // Kiểm tra xem phim đã có lịch chiếu chưa
                    if (!hasScheduledScreenings(movie.getMovieId())) {
                        throw new ValidateException("Cannot change status to NOW_SHOWING without scheduled screenings");
                    }
                } else if (newStatus == MovieStatus.ENDED) {
                    throw new ValidateException("Cannot change status directly from COMING_SOON to ENDED");
                }
                // Cho phép chuyển từ COMING_SOON sang CANCELLED mà không cần điều kiện
                // Cho phép chuyển từ COMING_SOON sang SUSPENDED nếu cần thiết
                break;

            case NOW_SHOWING:
                if (newStatus == MovieStatus.COMING_SOON) {
                    // Kiểm tra xem phim có vé đã bán chưa
                    if (hasTicketsSold(movie.getMovieId())) {
                        throw new ValidateException("Cannot change status back to COMING_SOON because tickets have been sold");
                    }
                } else if (newStatus == MovieStatus.SUSPENDED) {
                    // Cho phép chuyển sang SUSPENDED nhưng cần xử lý vé đã bán
                    if (hasActiveTickets(movie.getMovieId())) {
                        throw new ValidateException("Cannot suspend movie with active tickets. Handle existing tickets first");
                    }
                } else if (newStatus == MovieStatus.CANCELLED) {
                    // Kiểm tra xem phim có vé trong tương lai chưa
                    if (hasFutureTickets(movie.getMovieId())) {
                        throw new ValidateException("Cannot cancel movie with future tickets. Handle existing tickets first");
                    }
                }
                break;

            case ENDED:
                if (newStatus == MovieStatus.COMING_SOON) {
                    throw new ValidateException("Cannot change status from ENDED to COMING_SOON");
                } else if (newStatus == MovieStatus.NOW_SHOWING) {
                    // Kiểm tra xem có thể mở lại phim chiếu không
                    if (!canReopenMovie(movie.getMovieId())) {
                        throw new ValidateException("Cannot reopen movie that has ended more than 30 days ago");
                    }
                }
                break;

            case CANCELLED:
                if (newStatus == MovieStatus.COMING_SOON) {
                    // Có thể mở lại phim đã hủy nếu nó chưa từng bán vé
                    if (hasEverSoldTickets(movie.getMovieId())) {
                        throw new ValidateException("Cannot restart cancelled movie that has already sold tickets");
                    }
                } else if (newStatus == MovieStatus.NOW_SHOWING || newStatus == MovieStatus.ENDED) {
                    throw new ValidateException("Cannot move cancelled movie directly to NOW_SHOWING or ENDED status");
                }
                break;

            case SUSPENDED:
                if (newStatus == MovieStatus.COMING_SOON) {
                    // Không cho phép quay lại trạng thái COMING_SOON từ SUSPENDED
                    throw new ValidateException("Cannot change status from SUSPENDED to COMING_SOON");
                }
                // Cho phép chuyển từ SUSPENDED sang NOW_SHOWING, ENDED hoặc CANCELLED
                break;
        }
    }

    // Kiểm tra xem phim có lịch chiếu đã lên lịch chưa
    // Phương thức kiểm tra xem có vé đang hoạt động không
    // Phương thức kiểm tra xem có vé đang hoạt động không
    private boolean hasActiveTickets(Long movieId) {
        return bookingRepository.existsByScreening_Movie_MovieIdAndStatus(
                movieId, BookingStatus.CONFIRMED);
    }
    private boolean hasTicketsSold(Long movieId) {
        // Kiểm tra có vé nào đã được bán cho phim
        return bookingRepository.existsByScreening_Movie_MovieIdAndStatusIn(
                movieId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );
    }
    private boolean canReopenMovie(Long movieId) {
        // Lấy thời gian của suất chiếu cuối cùng
        LocalDateTime lastScreeningTime = screeningRepository.findLastScreeningTimeByMovieId(movieId);

        if (lastScreeningTime == null) {
            // Nếu không tìm thấy suất chiếu nào, không cho phép mở lại
            return false;
        }

        // Chỉ cho phép mở lại phim nếu suất chiếu cuối cùng trong vòng 30 ngày
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return lastScreeningTime.isAfter(thirtyDaysAgo);
    }
    private boolean hasScheduledScreenings(Long movieId) {
        // Kiểm tra có lịch chiếu nào cho phim này không, bao gồm cả trong quá khứ và tương lai
        return screeningRepository.existsByMovie_MovieId(movieId);
    }

    // Phương thức kiểm tra xem có vé trong tương lai không
    private boolean hasFutureTickets(Long movieId) {
        LocalDateTime now = LocalDateTime.now();
        return screeningRepository.existsByMovie_MovieIdAndStartTimeGreaterThan(
                movieId, now);
    }

    // Phương thức kiểm tra xem phim đã từng bán vé chưa
    private boolean hasEverSoldTickets(Long movieId) {
        return bookingRepository.existsByScreening_Movie_MovieId(movieId);
    }


    @Override
    public void deleteMovie(Long id) {
        movieRepository.findById(id)
                .ifPresentOrElse(movieRepository::delete,() -> {
                    throw new ResourceNotFoundException("Movie not found");
                });
    }

    @Override
    public MovieDto addImageToMovie(Long movieId, MultipartFile imageFile, boolean isPoster) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        // Upload image using imageService
        Image savedImage = imageService.uploadImage(imageFile);

        // Set isPoster flag
        savedImage.setPoster(isPoster);
        savedImage.setMovie(movie);

        // If this is a poster and another poster exists, update the old one
        if (isPoster) {
            Image existingPoster = movie.getPoster();
            if (existingPoster != null) {
                existingPoster.setPoster(false);
                imageReposioty.save(existingPoster);
            }
        }

        // Save updated image
        imageReposioty.save(savedImage);

        // Add image to movie's images collection
        movie.getImages().add(savedImage);
        Movie updatedMovie = movieRepository.save(movie);

        return mapToDto(updatedMovie, 0L);
    }

    @Override
    public void removeImageFromMovie(Long movieId, Long imageId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Image image = imageReposioty.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!image.getMovie().getMovieId().equals(movieId)) {
            throw new ValidateException("Image does not belong to this movie");
        }

        // Remove image from movie's collection
        movie.getImages().remove(image);
        movieRepository.save(movie);

        // Delete the image
        imageReposioty.delete(image);

        // If it was a poster, we might want to set another image as poster
        if (image.isPoster() && !movie.getImages().isEmpty()) {
            // Optionally set another image as poster
            Image newPoster = movie.getImages().iterator().next();
            newPoster.setPoster(true);
            imageReposioty.save(newPoster);
        }
    }

    @Override
    public Page<MovieDto> searchMovies(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCaseOrDirectorContainingIgnoreCase(
                keyword, keyword, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public Page<MovieDto> filterMoviesByGenre(String genre, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByGenreContainingIgnoreCase(genre, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public Page<MovieDto> filterMoviesByStatus(MovieStatus status, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByStatus(status, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public Page<MovieDto> filterMoviesByReleaseDate(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByReleaseDateBetween(startDate, endDate, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public Page<MovieDto> filterMoviesByLanguage(String language, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByLanguageContainingIgnoreCase(language, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public Page<MovieDto> filterMoviesByAgeRating(String ageRating, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByAgeRating(ageRating, pageable);
        return movies.map(movie -> mapToDto(movie, 0L));
    }

    @Override
    public MovieDto updateMovieStatus(Long id, MovieStatus status) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with ID " + id + " not found"));

        // Validate status transition
        validateStatusTransition(movie, status);

        // Update status
        movie.setStatus(status);
        Movie updatedMovie = movieRepository.save(movie);

        return mapToDto(updatedMovie, 0L);
    }

    @Override
    public List<MovieDto> getUpcomingMovies() {
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieRepository.findByStatusAndReleaseDateAfter(
                MovieStatus.COMING_SOON, today);

        return movies.stream()
                .map(movie -> mapToDto(movie, 0L))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> getNowShowingMovies() {
        List<Movie> movies = movieRepository.findByStatus(MovieStatus.NOW_SHOWING);

        return movies.stream()
                .map(movie -> mapToDto(movie, 0L))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> getTopRatedMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = movieRepository.findTopRatedMovies(pageable);

        return results.stream()
                .map(result -> {
                    Movie movie = (Movie) result[0];
                    Double avgRating = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;

                    MovieDto dto = mapToDto(movie, 0L);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> getMostViewedMovies(int limit) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = movieRepository.findMostViewedMoviesByTicketCount(pageable);

        return results.stream()
                .map(result -> {
                    Movie movie = (Movie) result[0];
                    Long ticketCount = result[1] == null ? 0L : ((Number) result[1]).longValue();

                    // Mapping từ entity sang DTO với số lượng vé
                    return mapToDto(movie, ticketCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MovieDto mapToDto(Movie movie, Long ticketCount) {
        MovieDto dto = new MovieDto();

        dto.setMovieId(movie.getMovieId());
        dto.setTitle(movie.getTitle());
        dto.setDirector(movie.getDirector());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setAgeRating(movie.getAgeRating());
        dto.setStatus(movie.getStatus());
        dto.setTicketCount(ticketCount);

        // Mapping poster nếu có
        Image posterImage = movie.getPoster();
        if (posterImage != null) {
            ImageDto posterDto = mapToDto(posterImage);
            dto.setPoster(posterDto);
        }
        return dto;
    }

    @Override
    public ImageDto mapToDto(Image image) {
        ImageDto dto = new ImageDto();

        dto.setId(image.getId());
        dto.setFileName(image.getFileName());
        dto.setFileType(image.getFileType());
        dto.setDownloadUrl(image.getDownloadUrl());
        dto.setPoster(image.isPoster());
        return dto;
    }
}
