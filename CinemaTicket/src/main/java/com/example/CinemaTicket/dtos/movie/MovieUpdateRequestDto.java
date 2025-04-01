package com.example.CinemaTicket.dtos.movie;

import com.example.CinemaTicket.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieUpdateRequestDto {
    private String genre;
    private String language;
    private String ageRating;
    private MovieStatus status;
}
