package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单项实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_item")
@Schema(description = "订单项实体类")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单项ID")
    private Long id;

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "零食ID")
    @NotNull(message = "零食ID不能为空")
    @TableField("snack_id")
    private Long snackId;

    @Schema(description = "购买数量")
    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须大于0")
    private Integer quantity;

    @Schema(description = "购买时单价（单位：分）")
    @NotNull(message = "购买时单价不能为空")
    @Positive(message = "购买时单价必须大于0")
    private Integer price;

    @Schema(description = "零食名称（冗余）")
    @NotBlank(message = "零食名称不能为空")
    @Size(max = 255, message = "零食名称长度不能超过255个字符")
    @TableField("snack_name")
    private String snackName;

    @Schema(description = "零食图片（冗余）")
    @Size(max = 500, message = "零食图片长度不能超过500个字符")
    @TableField("snack_image")
    private String snackImage;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
