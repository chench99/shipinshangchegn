package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.*;
import org.example.springboot.DTO.query.UserListQueryDTO;
import org.example.springboot.DTO.response.UserDetailResponseDTO;
import org.example.springboot.DTO.response.UserLoginResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.UserService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * @author system
 */
@Tag(name = "用户管理")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO loginDTO) {
        log.info("用户登录请求: {}", loginDTO.getUsername());
        UserLoginResponseDTO response = userService.login(loginDTO);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO registerDTO) {
        log.info("用户注册请求: {}", registerDTO.getUsername());
        UserDetailResponseDTO response = userService.register(registerDTO);
        return Result.success("注册成功", response);
    }




    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/current")
    public Result<UserDetailResponseDTO> getCurrentUser(HttpServletRequest request) {
        Long currentUserId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取当前用户信息: userId={}", currentUserId);
        UserDetailResponseDTO response = userService.getCurrentUser(currentUserId);
        return Result.success(response);
    }

    /**
     * 根据ID获取用户信息
     */
    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserDetailResponseDTO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("根据ID获取用户信息: userId={}", id);
        UserDetailResponseDTO response = userService.getUserById(id);
        return Result.success(response);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<UserDetailResponseDTO> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateCommandDTO updateDTO) {
        log.info("更新用户信息请求: userId={}", id);
        UserDetailResponseDTO response = userService.updateUser(id, updateDTO);
        return Result.success("更新成功", response);
    }

    /**
     * 修改用户密码
     */
    @Operation(summary = "修改用户密码")
    @PutMapping("/password/{id}")
    public Result<Void> updatePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        log.info("修改用户密码请求: userId={}", id);
        userService.updatePassword(id, passwordDTO);
        return Result.success();
    }

    /**
     * 用户退出登录
     */
    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long currentUserId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户退出登录: userId={}", currentUserId);
        // 这里可以添加token黑名单逻辑，目前简单返回成功
        return Result.success();
    }

    /**
     * 分页查询用户列表（管理员功能）
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<UserDetailResponseDTO>> getUserPage(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickname,
            @Parameter(description = "用户类型") @RequestParam(required = false) String userType,
            @Parameter(description = "用户状态") @RequestParam(required = false) String status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        // 权限检查：只有管理员可以查看用户列表
        String currentUserRole = JwtTokenUtils.getCurrentRoleFromRequest(request);
        if (!"ADMIN".equals(currentUserRole)) {
            return Result.error("权限不足");
        }

        UserListQueryDTO queryDTO = new UserListQueryDTO();
        queryDTO.setUsername(username);
        queryDTO.setNickname(nickname);
        queryDTO.setUserType(userType);
        queryDTO.setStatus(status);
        queryDTO.setCurrentPage(currentPage);
        queryDTO.setSize(size);

        log.info("管理员查询用户列表: page={}, size={}", currentPage, size);
        Page<UserDetailResponseDTO> response = userService.getUserPage(queryDTO);
        return Result.success(response);
    }




}
