package org.example.springboot.service.convert;

import org.example.springboot.DTO.response.ReviewResponseDTO;
import org.example.springboot.entity.Review;

public class ReviewConvert {
    public static ReviewResponseDTO toResponseDTO(Review review) {
        if (review == null) return null;
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setSnackId(review.getSnackId());
        dto.setUserId(review.getUserId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreateTime(review.getCreateTime());
        return dto;
    }
}


