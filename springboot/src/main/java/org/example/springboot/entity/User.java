package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
@Schema(description = "用户实体类")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 255, message = "密码长度必须在6到255个字符之间")
    private String password;

    @Schema(description = "昵称")
    @Size(max = 255, message = "昵称长度不能超过255个字符")
    private String nickname;

    @Schema(description = "头像URL")
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatar;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "用户类型")
    @TableField("user_type")
    private String userType;

    @Schema(description = "用户状态")
    private String status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.userType);
    }

    /**
     * 是否为普通用户
     */
    public boolean isUser() {
        return "USER".equals(this.userType);
    }

    /**
     * 是否为活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * 是否被封禁
     */
    public boolean isBanned() {
        return "BANNED".equals(this.status);
    }

    /**
     * 是否为非活跃状态
     */
    public boolean isInactive() {
        return "INACTIVE".equals(this.status);
    }

    /**
     * 获取显示名称（优先显示昵称，否则显示用户名）
     */
    public String getDisplayName() {
        return nickname != null && !nickname.trim().isEmpty() ? nickname : username;
    }

    /**
     * 获取用户类型显示名称
     */
    public String getUserTypeDisplayName() {
        if (userType == null) return "未知";
        switch (userType) {
            case "ADMIN":
                return "管理员";
            case "USER":
                return "普通用户";
            default:
                return "未知";
        }
    }

    /**
     * 获取用户状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) return "未知";
        switch (status) {
            case "ACTIVE":
                return "活跃";
            case "INACTIVE":
                return "非活跃";
            case "BANNED":
                return "封禁";
            default:
                return "未知";
        }
    }
}
