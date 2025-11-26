package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 购物车实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_cart")
@Schema(description = "购物车实体类")
public class Cart {

    @TableId(type = IdType.AUTO)
    @Schema(description = "购物车ID")
    private Long id;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "零食ID")
    @NotNull(message = "零食ID不能为空")
    @TableField("snack_id")
    private Long snackId;

    @Schema(description = "商品数量")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 检查是否为有效数量
     */
    public boolean isValidQuantity() {
        return this.quantity != null && this.quantity > 0;
    }

    /**
     * 检查是否属于指定用户
     */
    public boolean belongsToUser(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 增加商品数量
     */
    public void addQuantity(Integer amount) {
        if (amount != null && amount > 0) {
            this.quantity = (this.quantity == null ? 0 : this.quantity) + amount;
        }
    }

    /**
     * 设置商品数量（带校验）
     */
    public void setQuantityWithValidation(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
    }
}
