package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新命令DTO
 * @author system
 */
@Data
@Schema(description = "用户信息更新命令")
public class UserUpdateCommandDTO {


    @Schema(description = "昵称", example = "测试用户")
    @Size(max = 255, message = "昵称长度不能超过255个字符")
    private String nickname;

    @Schema(description = "头像", example = "/avatars/user.jpg")
    @Size(max = 500, message = "头像路径长度不能超过500个字符")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "用户类型", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String userType;

    @Schema(description = "用户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "BANNED"})
    private String status;
}
