package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新购物车商品数量命令DTO
 * @author system
 */
@Data
@Schema(description = "更新购物车商品数量命令")
public class CartUpdateCommandDTO {

    @Schema(description = "商品数量", example = "3")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
}
