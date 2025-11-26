package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.SnackCreateDTO;
import org.example.springboot.DTO.command.SnackUpdateDTO;
import org.example.springboot.DTO.response.SnackResponseDTO;
import org.example.springboot.DTO.response.SnackListResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.SnackService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 零食管理控制器
 * @author system
 */
@Tag(name = "零食管理")
@RestController
@Slf4j
@RequestMapping("/snack")
public class SnackController {

    @Resource
    private SnackService snackService;

    /**
     * 创建零食
     */
    @Operation(summary = "创建零食")
    @PostMapping
    public Result<SnackResponseDTO> createSnack(@Valid @RequestBody SnackCreateDTO createDTO) {
        log.info("创建零食请求: {}", createDTO);
        SnackResponseDTO response = snackService.createSnack(createDTO);
        return Result.success(response);
    }

    /**
     * 更新零食
     */
    @Operation(summary = "更新零食")
    @PutMapping("/{id}")
    public Result<SnackResponseDTO> updateSnack(
            @Parameter(description = "零食ID") @PathVariable Long id,
            @Valid @RequestBody SnackUpdateDTO updateDTO) {
        log.info("更新零食请求: snackId={}", id);
        updateDTO.setId(id); // 确保DTO中的ID与路径参数一致
        SnackResponseDTO response = snackService.updateSnack(updateDTO);
        return Result.success(response);
    }

    /**
     * 删除零食
     */
    @Operation(summary = "删除零食")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSnack(@Parameter(description = "零食ID") @PathVariable Long id) {
        log.info("删除零食请求: snackId={}", id);
        snackService.deleteSnack(id);
        return Result.success();
    }

    /**
     * 根据ID获取零食详情
     */
    @Operation(summary = "根据ID获取零食详情")
    @GetMapping("/{id}")
    public Result<SnackResponseDTO> getSnackById(@Parameter(description = "零食ID") @PathVariable Long id) {
        log.info("根据ID获取零食详情: snackId={}", id);
        SnackResponseDTO response = snackService.getSnackDetail(id);
        return Result.success(response);
    }

    /**
     * 后台分页查询零食列表
     */
    @Operation(summary = "后台分页查询零食列表")
    @GetMapping("/page")
    public Result<Page<SnackListResponseDTO>> getSnackPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "零食名称") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "零食状态") @RequestParam(required = false) String status,
            @Parameter(description = "最低价格（元）") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格（元）") @RequestParam(required = false) BigDecimal maxPrice) {
        
        log.info("后台分页查询零食列表: current={}, size={}, name={}, categoryId={}, status={}, priceRange={}-{}", 
                current, size, name, categoryId, status, minPrice, maxPrice);
        Page<SnackListResponseDTO> response = snackService.getSnackPage(current, size, name, categoryId, status, minPrice, maxPrice);
        return Result.success(response);
    }

    /**
     * 前台分页查询零食列表（只显示上架商品）
     */
    @Operation(summary = "前台分页查询零食列表")
    @GetMapping("/frontend/page")
    public Result<Page<SnackListResponseDTO>> getFrontendSnackPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "12") Long size,
            @Parameter(description = "零食名称") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder,
            @Parameter(description = "最低价格（元）") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格（元）") @RequestParam(required = false) BigDecimal maxPrice) {
        
        log.info("前台分页查询零食列表: current={}, size={}, name={}, categoryId={}, sort={} {}, priceRange={}-{}", 
                current, size, name, categoryId, sortBy, sortOrder, minPrice, maxPrice);
        Page<SnackListResponseDTO> response = snackService.getFrontendSnackPage(current, size, name, categoryId, sortBy, sortOrder, minPrice, maxPrice);
        return Result.success(response);
    }

    /**
     * 获取推荐零食列表（首页展示）
     */
    @Operation(summary = "获取推荐零食列表")
    @GetMapping("/recommended")
    public Result<List<SnackListResponseDTO>> getRecommendedSnacks(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "8") Integer limit) {
        
        log.info("获取推荐零食列表: limit={}", limit);
        List<SnackListResponseDTO> response = snackService.getRecommendedSnacks(limit);
        return Result.success(response);
    }

    /**
     * 更新零食状态
     */
    @Operation(summary = "更新零食状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateSnackStatus(
            @Parameter(description = "零食ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        
        log.info("更新零食状态请求: snackId={}, status={}", id, status);
        snackService.updateSnackStatus(id, status);
        return Result.success();
    }

    /**
     * 根据分类ID获取零食列表
     */
    @Operation(summary = "根据分类ID获取零食列表")
    @GetMapping("/category/{categoryId}")
    public Result<List<SnackListResponseDTO>> getSnacksByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        
        log.info("根据分类ID获取零食列表: categoryId={}, limit={}", categoryId, limit);
        Page<SnackListResponseDTO> page = snackService.getFrontendSnackPage(1L, limit.longValue(), null, categoryId, "create_time", "desc", null, null);
        return Result.success(page.getRecords());
    }

    /**
     * 搜索零食（前台搜索功能）
     */
    @Operation(summary = "搜索零食")
    @GetMapping("/search")
    public Result<Page<SnackListResponseDTO>> searchSnacks(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "12") Long size,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder) {
        
        log.info("搜索零食: current={}, size={}, keyword={}, categoryId={}, sort={} {}", 
                current, size, keyword, categoryId, sortBy, sortOrder);
        Page<SnackListResponseDTO> response = snackService.getFrontendSnackPage(current, size, keyword, categoryId, sortBy, sortOrder, null, null);
        return Result.success(response);
    }
}
