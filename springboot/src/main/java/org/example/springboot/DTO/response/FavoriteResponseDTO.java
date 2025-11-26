package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏响应DTO")
public class FavoriteResponseDTO {

    @Schema(description = "收藏ID")
    private Long favoriteId;

    @Schema(description = "零食ID")
    private Long snackId;

    @Schema(description = "零食名称")
    private String snackName;

    @Schema(description = "零食描述")
    private String snackDescription;

    @Schema(description = "价格（元）")
    private BigDecimal price;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @Schema(description = "商品状态")
    private String status;

    @Schema(description = "销售数量")
    private Integer salesCount;

    @Schema(description = "收藏数量")
    private Integer favoriteCount;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    @Schema(description = "是否可购买")
    private Boolean canPurchase;
}
