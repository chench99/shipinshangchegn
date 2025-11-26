package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "评价响应DTO")
public class ReviewResponseDTO {
    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "零食ID")
    private Long snackId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "评分")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "图片列表URL")
    private List<String> images;
}


