package com.example.CinemaTicket.models;


import com.example.CinemaTicket.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(nullable = false)
    private String title;

    private String director;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Integer duration;

    @Column(length = 4000)
    private String description;

    private String genre;

    private String language;

    @Column(name = "age_rating")
    private String ageRating;

    // Bỏ trường posterUrl

    @Column(name = "movie_status")
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Screening> screenings = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<MovieRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<MovieActor> movieActors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();

    // Phương thức tiện ích để lấy poster
    public Image getPoster() {
        return images.stream()
                .filter(Image::isPoster)
                .findFirst()
                .orElse(null);
    }
}