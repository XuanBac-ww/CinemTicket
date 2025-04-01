package com.example.CinemaTicket.service.image;

import com.example.CinemaTicket.exceptions.FileStorageException;
import com.example.CinemaTicket.models.Image;
import com.example.CinemaTicket.repository.ImageReposioty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageReposioty imageRepository;

    @Override
    public Image uploadImage(MultipartFile imageFile) {
        // Validate file
        if (imageFile == null || imageFile.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        try {
            // Normalize file name and add uuid to avoid duplicates
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            String fileExtension = getFileExtension(originalFileName);
            String fileName = UUID.randomUUID() + "." + fileExtension;

            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence: " + originalFileName);
            }

            // Validate file type
            validateImageType(imageFile);

            // Create image entity
            Image image = new Image();
            image.setFileName(fileName);
            image.setFileType(imageFile.getContentType());

            // Convert MultipartFile to Blob
            Blob blob = new SerialBlob(imageFile.getBytes());
            image.setImage(blob);

            // Create download URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/images/")
                    .path(fileName)
                    .toUriString();
            image.setDownloadUrl(fileDownloadUri);

            // Save to database
            log.info("Saving image with name: {}", fileName);
            return imageRepository.save(image);

        } catch (IOException | SQLException ex) {
            log.error("Failed to store file", ex);
            throw new FileStorageException("Failed to store file: " + ex.getMessage());
        }
    }

    @Override
    public Blob getImageBlob(String fileName) {
        Image image = imageRepository.findByFileName(fileName)
                .orElseThrow(() -> new FileStorageException("Image not found with name: " + fileName));
        return image.getImage();
    }

    @Override
    public Image getImageByFileName(String fileName) {
        return imageRepository.findByFileName(fileName)
                .orElseThrow(() -> new FileStorageException("Image not found with name: " + fileName));
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new FileStorageException("Image not found with id: " + imageId));

        imageRepository.delete(image);
        log.info("Deleted image with id: {}", imageId);
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("File must be an image");
        }
    }
}
