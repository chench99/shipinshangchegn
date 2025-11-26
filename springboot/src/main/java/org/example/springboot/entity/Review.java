package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 零食评价实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_review")
@Schema(description = "零食评价实体类")
public class Review {

    @TableId(type = IdType.AUTO)
    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "零食ID")
    @NotNull(message = "零食ID不能为空")
    @TableField("snack_id")
    private Long snackId;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "评分(1-5)")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer rating;

    @Schema(description = "评价内容")
    @NotBlank(message = "评价内容不能为空")
    private String content;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}


