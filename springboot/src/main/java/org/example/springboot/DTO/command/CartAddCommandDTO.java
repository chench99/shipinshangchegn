package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加商品到购物车命令DTO
 * @author system
 */
@Data
@Schema(description = "添加商品到购物车命令")
public class CartAddCommandDTO {

    @Schema(description = "零食ID", example = "1")
    @NotNull(message = "零食ID不能为空")
    private Long snackId;

    @Schema(description = "商品数量", example = "2")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
}
