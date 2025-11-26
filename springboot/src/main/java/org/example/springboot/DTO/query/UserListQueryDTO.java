package org.example.springboot.DTO.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户列表查询DTO
 * @author system
 */
@Data
@Schema(description = "用户列表查询")
public class UserListQueryDTO {

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;


    @Schema(description = "昵称（模糊查询）", example = "管理员")
    private String nickname;

    @Schema(description = "用户类型", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
    private String userType;

    @Schema(description = "用户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "BANNED"})
    private String status;

    @Schema(description = "当前页码", example = "1")
    private Integer currentPage = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
