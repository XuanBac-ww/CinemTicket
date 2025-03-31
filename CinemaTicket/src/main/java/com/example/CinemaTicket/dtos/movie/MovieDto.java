package com.example.CinemaTicket.dtos.movie;


import com.example.CinemaTicket.dtos.ImageDto;
import com.example.CinemaTicket.dtos.MovieActorDTO;
import com.example.CinemaTicket.dtos.MovieRatingDTO;
import com.example.CinemaTicket.dtos.ScreeningDTO;
import com.example.CinemaTicket.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long movieId;
    private String title;
    private String director;
    private LocalDate releaseDate;
    private Integer duration;
    private String genre;
    private String language;
    private String ageRating;
    private MovieStatus status;
    private Long ticketCount;
    private ImageDto poster; // Chỉ giữ poster, không cần toàn bộ images
}
