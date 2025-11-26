package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量删除购物车商品命令DTO
 * @author system
 */
@Data
@Schema(description = "批量删除购物车商品命令")
public class CartBatchDeleteCommandDTO {

    @Schema(description = "购物车ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "购物车ID列表不能为空")
    private List<Long> cartIds;
}
