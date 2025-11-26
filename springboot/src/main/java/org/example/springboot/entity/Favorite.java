package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品收藏实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_favorite")
@Schema(description = "商品收藏实体类")
public class Favorite {

    @TableId(type = IdType.AUTO)
    @Schema(description = "收藏ID")
    private Long id;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "零食ID")
    @NotNull(message = "零食ID不能为空")
    @TableField("snack_id")
    private Long snackId;

    @Schema(description = "收藏时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 检查是否属于指定用户
     */
    public boolean belongsToUser(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 检查是否收藏了指定商品
     */
    public boolean isSnackFavorited(Long snackId) {
        return this.snackId != null && this.snackId.equals(snackId);
    }
}
