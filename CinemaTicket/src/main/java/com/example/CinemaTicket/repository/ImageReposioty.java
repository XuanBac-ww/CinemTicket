package com.example.CinemaTicket.repository;

import com.example.CinemaTicket.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageReposioty extends JpaRepository<Image,Long> {
    /**
     * Find an image by its file name
     *
     * @param fileName the name of the file
     * @return Optional containing the Image if found
     */
    Optional<Image> findByFileName(String fileName);

    /**
     * Find all images associated with a specific movie
     *
     * @param movieId the ID of the movie
     * @return List of images for the movie
     */
    List<Image> findByMovie_MovieId(Long movieId);

    /**
     * Find the poster image for a specific movie
     *
     * @param movieId the ID of the movie
     * @return Optional containing the poster Image if found
     */
    Optional<Image> findByMovie_MovieIdAndIsPosterTrue(Long movieId);

    /**
     * Count how many images a movie has
     *
     * @param movieId the ID of the movie
     * @return the count of images
     */
    long countByMovie_MovieId(Long movieId);
}
