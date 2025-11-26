package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 零食分类实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_category")
@Schema(description = "零食分类实体类")
public class Category {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 1, max = 100, message = "分类名称长度必须在1到100个字符之间")
    private String name;

    @Schema(description = "排序值，越小越靠前")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "分类状态")
    private String status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否为活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * 是否为非活跃状态
     */
    public boolean isInactive() {
        return "INACTIVE".equals(this.status);
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) return "未知";
        switch (status) {
            case "ACTIVE":
                return "启用";
            case "INACTIVE":
                return "禁用";
            default:
                return "未知";
        }
    }
}
