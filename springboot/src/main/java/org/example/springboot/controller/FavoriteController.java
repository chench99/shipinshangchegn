package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.FavoriteToggleCommandDTO;
import org.example.springboot.DTO.response.FavoriteResponseDTO;
import org.example.springboot.DTO.response.FavoriteStatusResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.FavoriteService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品收藏控制器
 * @author system
 */
@Tag(name = "商品收藏管理")
@RestController
@Slf4j
@RequestMapping("/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    @Operation(summary = "切换收藏状态")
    @PostMapping("/toggle")
    public Result<FavoriteStatusResponseDTO> toggleFavorite(
            @Valid @RequestBody FavoriteToggleCommandDTO commandDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}切换商品{}的收藏状态", userId, commandDTO.getSnackId());
        
        FavoriteStatusResponseDTO response = favoriteService.toggleFavorite(commandDTO.getSnackId(), userId);
        String message = response.getIsFavorited() ? "收藏成功" : "取消收藏成功";
        return Result.success(message, response);
    }

    /**
     * 检查商品收藏状态
     */
    @Operation(summary = "检查商品收藏状态")
    @GetMapping("/status/{snackId}")
    public Result<FavoriteStatusResponseDTO> checkFavoriteStatus(
            @Parameter(description = "商品ID") @PathVariable Long snackId,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("检查用户{}对商品{}的收藏状态", userId, snackId);
        
        FavoriteStatusResponseDTO response = favoriteService.checkFavoriteStatus(snackId, userId);
        return Result.success(response);
    }

    /**
     * 获取用户收藏列表（分页）
     */
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/list")
    public Result<Page<FavoriteResponseDTO>> getFavoriteList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的收藏列表，页码: {}, 每页大小: {}", userId, current, size);
        
        Page<FavoriteResponseDTO> response = favoriteService.getUserFavoriteList(userId, current, size);
        return Result.success(response);
    }

    /**
     * 获取用户收藏总数
     */
    @Operation(summary = "获取用户收藏总数")
    @GetMapping("/count")
    public Result<Integer> getFavoriteCount(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的收藏总数", userId);
        
        Integer count = favoriteService.getUserFavoriteCount(userId);
        return Result.success(count);
    }

    /**
     * 批量检查商品收藏状态
     */
    @Operation(summary = "批量检查商品收藏状态")
    @PostMapping("/batch-status")
    public Result<Map<Long, Boolean>> batchCheckFavoriteStatus(
            @Parameter(description = "商品ID列表") @RequestBody List<Long> snackIds,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("批量检查用户{}对商品{}的收藏状态", userId, snackIds);
        
        Map<Long, Boolean> response = favoriteService.batchCheckFavoriteStatus(snackIds, userId);
        return Result.success(response);
    }
}
