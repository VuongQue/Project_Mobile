package com.example.s_parking.implement;

import com.example.s_parking.dto.response.ImageResponse;
import com.example.s_parking.entity.Image;
import com.example.s_parking.repository.ImageRepository;
import com.example.s_parking.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageImp implements ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Override
    public List<Image> getImages() {
        return imageRepository.findAll();
    }

    @Override
    public ImageResponse convertToDto(Image entity) {
        return ImageResponse.builder()
                .id(entity.getId())
                .link(entity.getLink())
                .build();
    }

    @Override
    public List<ImageResponse> convertAllToDto(List<Image> list) {
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
