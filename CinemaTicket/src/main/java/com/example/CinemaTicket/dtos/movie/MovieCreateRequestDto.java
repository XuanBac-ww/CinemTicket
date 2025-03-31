package com.example.CinemaTicket.dtos.movie;

import com.example.CinemaTicket.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCreateRequestDto {
    private String title;
    private String director;
    private LocalDate releaseDate;
    private Integer duration;
    private String description;
    private String genre;
    private String language;
    private String ageRating;
    private MovieStatus status;
}
