package com.example.s_parking.service;

import com.example.s_parking.dto.response.ImageResponse;
import com.example.s_parking.entity.Image;

import java.util.List;

public interface ImageService {
    List<Image> getImages();

    ImageResponse convertToDto(Image entity);

    List<ImageResponse> convertAllToDto(List<Image> list);
}
