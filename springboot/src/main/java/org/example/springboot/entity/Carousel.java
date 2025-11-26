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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_carousel")
@Schema(description = "轮播图实体")
public class Carousel {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标题")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Schema(description = "跳转类型：URL/PRODUCT/NONE")
    @NotBlank(message = "跳转类型不能为空")
    @TableField("jump_type")
    private String jumpType;

    @Schema(description = "跳转目标：URL或商品ID")
    @Size(max = 512, message = "跳转目标长度不能超过512个字符")
    @TableField("jump_target")
    private String jumpTarget;

    @Schema(description = "排序值，越小越靠前")
    @NotNull(message = "排序不能为空")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态：ENABLED/DISABLED")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "生效开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}

