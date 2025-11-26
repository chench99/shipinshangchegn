package org.example.springboot.service.convert;

import org.example.springboot.DTO.command.CategoryCreateDTO;
import org.example.springboot.DTO.command.CategoryUpdateDTO;
import org.example.springboot.DTO.response.CategoryResponseDTO;
import org.example.springboot.entity.Category;
import org.example.springboot.enumClass.CategoryStatus;

import java.time.LocalDateTime;

/**
 * 分类转换类
 * @author system
 */
public class CategoryConvert {

    /**
     * 创建命令DTO转换为Category实体
     * @param createDTO 创建命令DTO
     * @return Category实体
     */
    public static Category createCommandToEntity(CategoryCreateDTO createDTO) {
        return Category.builder()
                .name(createDTO.getName())
                .sortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0)
                .status(createDTO.getStatus() != null ? createDTO.getStatus() : CategoryStatus.ACTIVE.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为Category实体（用于更新操作）
     * @param updateDTO 更新命令DTO
     * @return Category实体
     */
    public static Category updateCommandToEntity(CategoryUpdateDTO updateDTO) {
        return Category.builder()
                .id(updateDTO.getId())
                .name(updateDTO.getName())
                .sortOrder(updateDTO.getSortOrder())
                .status(updateDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * Category实体转换为响应DTO
     * @param category Category实体
     * @return 分类响应DTO
     */
    public static CategoryResponseDTO entityToResponse(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .sortOrder(category.getSortOrder())
                .status(category.getStatus())
                .statusDisplayName(category.getStatusDisplayName())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }
}
