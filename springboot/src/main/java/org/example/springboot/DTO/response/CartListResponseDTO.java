package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车列表响应DTO
 * @author system
 */
@Data
@Schema(description = "购物车列表响应")
public class CartListResponseDTO {

    @Schema(description = "购物车商品项列表")
    private List<CartItemResponseDTO> items;

    @Schema(description = "商品总数量")
    private Integer totalQuantity;

    @Schema(description = "商品总金额（元）")
    private BigDecimal totalAmount;

    @Schema(description = "有效商品数量（可购买的商品数量）")
    private Integer validQuantity;

    @Schema(description = "有效商品金额（可购买的商品金额）")
    private BigDecimal validAmount;

    @Schema(description = "是否为空购物车")
    private Boolean isEmpty;

    /**
     * 计算统计信息
     */
    public void calculateStatistics() {
        if (items == null || items.isEmpty()) {
            this.isEmpty = true;
            this.totalQuantity = 0;
            this.totalAmount = BigDecimal.ZERO;
            this.validQuantity = 0;
            this.validAmount = BigDecimal.ZERO;
            return;
        }

        this.isEmpty = false;
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
        this.validQuantity = 0;
        this.validAmount = BigDecimal.ZERO;

        for (CartItemResponseDTO item : items) {
            // 计算总数量和总金额
            this.totalQuantity += item.getQuantity();
            this.totalAmount = this.totalAmount.add(item.getSubtotal());

            // 计算有效数量和有效金额（仅可购买的商品）
            if (Boolean.TRUE.equals(item.getCanPurchase())) {
                this.validQuantity += item.getQuantity();
                this.validAmount = this.validAmount.add(item.getSubtotal());
            }
        }
    }
}
