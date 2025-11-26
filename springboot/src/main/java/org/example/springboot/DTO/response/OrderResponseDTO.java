package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 * @author system
 */
@Data
@Schema(description = "订单响应DTO")
public class OrderResponseDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货地址ID")
    private Long addressId;

    @Schema(description = "订单总金额（单位：分）")
    private Integer totalAmount;

    @Schema(description = "订单总金额（单位：元）")
    private String totalAmountYuan;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "收货地址信息")
    private AddressResponseDTO address;

    @Schema(description = "订单项列表")
    private List<OrderItemResponseDTO> orderItems;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;
}
