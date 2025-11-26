package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 零食列表响应DTO（简化版，用于列表展示）
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "零食列表响应")
public class SnackListResponseDTO {

    @Schema(description = "零食ID", example = "1")
    private Long id;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "膨化食品")
    private String categoryName;

    @Schema(description = "零食名称", example = "薯片原味")
    private String name;

    @Schema(description = "零食描述", example = "香脆可口的原味薯片，精选优质土豆制作")
    private String description;

    @Schema(description = "价格（元）", example = "5.99")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "封面图片URL", example = "/files/snack/cover/1.jpg")
    private String coverImage;

    @Schema(description = "零食状态", example = "ON_SALE")
    private String status;

    @Schema(description = "零食状态显示名称", example = "上架")
    private String statusDisplayName;

    @Schema(description = "销售数量", example = "0")
    private Integer salesCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否上架", example = "true")
    private Boolean onSale;

    @Schema(description = "是否有库存", example = "true")
    private Boolean hasStock;

    @Schema(description = "是否可以购买", example = "true")
    private Boolean canPurchase;
}

