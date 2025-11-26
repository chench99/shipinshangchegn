package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "轮播图创建DTO")
public class CarouselCreateDTO {
    @Size(max = 255)
    private String title;

    @NotBlank
    private String jumpType; // URL/PRODUCT/NONE

    @Size(max = 512)
    private String jumpTarget; // URL或商品ID字符串

    @NotNull
    private Integer sortOrder;

    @NotBlank
    private String status; // ENABLED/DISABLED

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}


