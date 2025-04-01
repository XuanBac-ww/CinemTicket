package com.example.CinemaTicket.service.image;

import com.example.CinemaTicket.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

public interface ImageService {
    Image uploadImage(MultipartFile imageFile);

    Blob getImageBlob(String fileName);

    Image getImageByFileName(String fileName);

    void deleteImage(Long imageId);
}
