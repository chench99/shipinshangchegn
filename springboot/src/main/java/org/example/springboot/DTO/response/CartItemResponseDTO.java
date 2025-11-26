package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车商品项响应DTO
 * @author system
 */
@Data
@Schema(description = "购物车商品项响应")
public class CartItemResponseDTO {

    @Schema(description = "购物车ID")
    private Long id;

    @Schema(description = "零食ID")
    private Long snackId;

    @Schema(description = "零食名称")
    private String snackName;

    @Schema(description = "零食描述")
    private String snackDescription;

    @Schema(description = "零食封面图片URL")
    private String snackImage;

    @Schema(description = "零食单价（元）")
    private BigDecimal price;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "零食库存")
    private Integer stock;

    @Schema(description = "零食状态")
    private String status;

    @Schema(description = "小计金额（元）")
    private BigDecimal subtotal;

    @Schema(description = "是否可购买（库存充足且上架）")
    private Boolean canPurchase;

    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 计算小计金额
     */
    public void calculateSubtotal() {
        if (this.price != null && this.quantity != null) {
            this.subtotal = this.price.multiply(new BigDecimal(this.quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    /**
     * 判断是否可购买
     */
    public void checkCanPurchase() {
        this.canPurchase = this.stock != null && 
                          this.stock >= this.quantity && 
                          "ON_SALE".equals(this.status);
    }
}
