package com.example.s_parking.controller;

import com.example.s_parking.dto.response.ImageResponse;
import com.example.s_parking.entity.Image;
import com.example.s_parking.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("img/")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllImages() {
        List<Image> images = imageService.getImages();
        List<ImageResponse> imageResponses = imageService.convertAllToDto(images);

        if (imageResponses == null || imageResponses.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No images found");
        }
        return ResponseEntity.ok(imageResponses);
    }
}
