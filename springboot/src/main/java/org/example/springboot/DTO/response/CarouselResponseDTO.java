package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "轮播图响应DTO")
public class CarouselResponseDTO {
    private Long id;
    private String title;
    private String jumpType;
    private String jumpTarget;
    private Integer sortOrder;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String imageUrl; // 关联图片访问路径（从文件中心查询）
}


