package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 零食实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_snack")
@Schema(description = "零食实体类")
public class Snack {

    @TableId(type = IdType.AUTO)
    @Schema(description = "零食ID")
    private Long id;

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "零食名称")
    @NotBlank(message = "零食名称不能为空")
    @Size(max = 255, message = "零食名称长度不能超过255个字符")
    private String name;

    @Schema(description = "零食描述")
    @Size(max = 2000, message = "零食描述长度不能超过2000个字符")
    private String description;

    @Schema(description = "价格（单位：分）")
    @NotNull(message = "价格不能为空")
    @Min(value = 1, message = "价格必须大于0")
    private Integer price;

    @Schema(description = "库存数量")
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;

    @Schema(description = "封面图片URL")
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    @TableField("cover_image")
    private String coverImage;

    @Schema(description = "详情图片URL列表 (JSON数组)")
    @TableField("detail_images")
    private String detailImages;

    @Schema(description = "状态（ON_SALE, OFF_SHELF）")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "销售数量")
    @Min(value = 0, message = "销售数量不能为负数")
    @TableField("sales_count")
    private Integer salesCount;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否上架状态
     */
    public boolean isOnSale() {
        return "ON_SALE".equals(this.status);
    }

    /**
     * 是否下架状态
     */
    public boolean isOffShelf() {
        return "OFF_SHELF".equals(this.status);
    }

    /**
     * 是否有库存
     */
    public boolean hasStock() {
        return this.stock != null && this.stock > 0;
    }

    /**
     * 获取价格（元）
     */
    public BigDecimal getPriceInYuan() {
        if (this.price == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(this.price).divide(BigDecimal.valueOf(100));
    }

    /**
     * 设置价格（元）
     */
    public void setPriceInYuan(BigDecimal priceInYuan) {
        if (priceInYuan != null) {
            this.price = priceInYuan.multiply(BigDecimal.valueOf(100)).intValue();
        }
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) return "未知";
        switch (status) {
            case "ON_SALE":
                return "上架";
            case "OFF_SHELF":
                return "下架";
            default:
                return "未知";
        }
    }

    /**
     * 是否可以购买（上架且有库存）
     */
    public boolean canPurchase() {
        return isOnSale() && hasStock();
    }
}

