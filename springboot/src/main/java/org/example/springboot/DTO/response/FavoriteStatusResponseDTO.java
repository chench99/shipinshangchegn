package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏状态响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏状态响应DTO")
public class FavoriteStatusResponseDTO {

    @Schema(description = "零食ID")
    private Long snackId;

    @Schema(description = "是否已收藏")
    private Boolean isFavorited;

    @Schema(description = "收藏总数")
    private Integer favoriteCount;
}
