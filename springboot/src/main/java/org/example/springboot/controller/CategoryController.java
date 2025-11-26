package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.CategoryCreateDTO;
import org.example.springboot.DTO.command.CategoryUpdateDTO;
import org.example.springboot.DTO.response.CategoryResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 * @author system
 */
@Tag(name = "分类管理")
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 创建分类
     */
    @Operation(summary = "创建分类")
    @PostMapping
    public Result<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryCreateDTO createDTO) {
        log.info("创建分类请求: {}", createDTO);
        CategoryResponseDTO response = categoryService.createCategory(createDTO);
        return Result.success(response);
    }

    /**
     * 更新分类
     */
    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<CategoryResponseDTO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        log.info("更新分类请求: categoryId={}", id);
        updateDTO.setId(id); // 确保DTO中的ID与路径参数一致
        CategoryResponseDTO response = categoryService.updateCategory(updateDTO);
        return Result.success(response);
    }

    /**
     * 删除分类
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("删除分类请求: categoryId={}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 根据ID获取分类详情
     */
    @Operation(summary = "根据ID获取分类详情")
    @GetMapping("/{id}")
    public Result<CategoryResponseDTO> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("根据ID获取分类详情: categoryId={}", id);
        CategoryResponseDTO response = categoryService.getCategoryDetail(id);
        return Result.success(response);
    }

    /**
     * 分页查询分类列表
     */
    @Operation(summary = "分页查询分类列表")
    @GetMapping("/page")
    public Result<Page<CategoryResponseDTO>> getCategoryPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name,
            @Parameter(description = "分类状态") @RequestParam(required = false) String status) {
        
        log.info("分页查询分类列表: current={}, size={}, name={}, status={}", current, size, name, status);
        Page<CategoryResponseDTO> response = categoryService.getCategoryPage(current, size, name, status);
        return Result.success(response);
    }

    /**
     * 获取所有启用状态的分类列表
     */
    @Operation(summary = "获取所有启用状态的分类列表")
    @GetMapping("/active")
    public Result<List<CategoryResponseDTO>> getActiveCategoryList() {
        log.info("获取所有启用状态的分类列表");
        List<CategoryResponseDTO> response = categoryService.getActiveCategoryList();
        return Result.success(response);
    }

    /**
     * 更新分类状态
     */
    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        log.info("更新分类状态请求: categoryId={}, status={}", id, status);
        categoryService.updateCategoryStatus(id, status);
        return Result.success();
    }
}
