package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单项响应DTO
 * @author system
 */
@Data
@Schema(description = "订单项响应DTO")
public class OrderItemResponseDTO {

    @Schema(description = "订单项ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "零食ID")
    private Long snackId;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "购买时单价（单位：分）")
    private Integer price;

    @Schema(description = "购买时单价（单位：元）")
    private String priceYuan;

    @Schema(description = "小计金额（单位：分）")
    private Integer subtotal;

    @Schema(description = "小计金额（单位：元）")
    private String subtotalYuan;

    @Schema(description = "零食名称")
    private String snackName;

    @Schema(description = "零食图片")
    private String snackImage;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
