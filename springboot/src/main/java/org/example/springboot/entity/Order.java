package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order")
@Schema(description = "订单实体类")
public class Order {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    @Size(max = 100, message = "订单号长度不能超过100个字符")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "收货地址ID")
    @NotNull(message = "收货地址ID不能为空")
    @TableField("address_id")
    private Long addressId;

    @Schema(description = "订单总金额（单位：分）")
    @NotNull(message = "订单总金额不能为空")
    @TableField("total_amount")
    private Integer totalAmount;

    @Schema(description = "订单状态")
    @NotBlank(message = "订单状态不能为空")
    @Size(max = 50, message = "订单状态长度不能超过50个字符")
    private String status;

    @Schema(description = "订单备注")
    @Size(max = 500, message = "订单备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    @TableField("ship_time")
    private LocalDateTime shipTime;

    @Schema(description = "完成时间")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    @Schema(description = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;
}
