package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 订单创建DTO
 * @author system
 */
@Data
@Schema(description = "订单创建DTO")
public class OrderCreateDTO {

    @Schema(description = "订单类型", example = "CART_ORDER(购物车下单) | DIRECT_ORDER(直接下单)")
    @NotNull(message = "订单类型不能为空")
    private String orderType;

    @Schema(description = "收货地址ID")
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @Schema(description = "订单备注")
    @Size(max = 500, message = "订单备注长度不能超过500个字符")
    private String remark;

    // 购物车下单相关字段
    @Schema(description = "购物车商品ID列表（购物车下单时使用）")
    private List<Long> cartItemIds;

    // 直接下单相关字段
    @Schema(description = "直接购买商品信息（直接下单时使用）")
    @Valid
    private DirectOrderItem directOrderItem;

    /**
     * 直接下单商品信息
     */
    @Data
    @Schema(description = "直接下单商品信息")
    public static class DirectOrderItem {
        @Schema(description = "零食ID")
        @NotNull(message = "零食ID不能为空")
        private Long snackId;

        @Schema(description = "购买数量")
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;
    }
}
