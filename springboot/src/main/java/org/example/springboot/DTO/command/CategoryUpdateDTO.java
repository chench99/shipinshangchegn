package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类更新命令DTO
 * @author system
 */
@Data
@Schema(description = "分类更新命令")
public class CategoryUpdateDTO {

    @Schema(description = "分类ID", example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long id;

    @Schema(description = "分类名称", example = "膨化食品")
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 1, max = 100, message = "分类名称长度必须在1到100个字符之间")
    private String name;

    @Schema(description = "排序值，越小越靠前", example = "1")
    private Integer sortOrder;

    @Schema(description = "分类状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
