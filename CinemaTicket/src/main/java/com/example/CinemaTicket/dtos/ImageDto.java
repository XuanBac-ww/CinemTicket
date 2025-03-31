package com.example.CinemaTicket.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private boolean isPoster;
}
