package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 零食更新命令DTO
 * @author system
 */
@Data
@Schema(description = "零食更新命令")
public class SnackUpdateDTO {

    @Schema(description = "零食ID", example = "1")
    @NotNull(message = "零食ID不能为空")
    private Long id;

    @Schema(description = "分类ID", example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "零食名称", example = "薯片原味")
    @NotBlank(message = "零食名称不能为空")
    @Size(min = 1, max = 255, message = "零食名称长度必须在1到255个字符之间")
    private String name;

    @Schema(description = "零食描述", example = "香脆可口的原味薯片，精选优质土豆制作")
    @Size(max = 2000, message = "零食描述长度不能超过2000个字符")
    private String description;

    @Schema(description = "价格（元）", example = "5.99")
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负数")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;

    @Schema(description = "封面图片URL", example = "/files/snack/cover/1.jpg")
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImage;

    @Schema(description = "详情图片URL列表")
    private List<String> detailImages;

    @Schema(description = "零食状态", example = "ON_SALE", allowableValues = {"ON_SALE", "OFF_SHELF"})
    @NotBlank(message = "零食状态不能为空")
    private String status;
}

