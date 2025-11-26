package org.example.springboot.service.convert;

import org.example.springboot.DTO.command.CarouselCreateDTO;
import org.example.springboot.DTO.command.CarouselUpdateDTO;
import org.example.springboot.DTO.response.CarouselResponseDTO;
import org.example.springboot.entity.Carousel;

public class CarouselConvert {

    public static Carousel toEntity(CarouselCreateDTO dto) {
        if (dto == null) return null;
        Carousel entity = new Carousel();
        entity.setTitle(dto.getTitle());
        entity.setJumpType(dto.getJumpType());
        entity.setJumpTarget(dto.getJumpTarget());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        return entity;
    }

    public static void copyToEntity(CarouselUpdateDTO dto, Carousel entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setJumpType(dto.getJumpType());
        entity.setJumpTarget(dto.getJumpTarget());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }

    public static CarouselResponseDTO toResponse(Carousel entity) {
        if (entity == null) return null;
        CarouselResponseDTO dto = new CarouselResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setJumpType(entity.getJumpType());
        dto.setJumpTarget(entity.getJumpTarget());
        dto.setSortOrder(entity.getSortOrder());
        dto.setStatus(entity.getStatus());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        return dto;
    }
}


