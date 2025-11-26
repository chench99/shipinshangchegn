package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.CarouselCreateDTO;
import org.example.springboot.DTO.command.CarouselUpdateDTO;
import org.example.springboot.DTO.response.CarouselResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.CarouselService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/carousel")
@Slf4j
public class CarouselController {

    @Resource
    private CarouselService carouselService;

    @Operation(summary = "创建轮播图")
    @PostMapping
    public Result<CarouselResponseDTO> create(@Valid @RequestBody CarouselCreateDTO createDTO) {
        log.info("创建轮播: {}", createDTO);
        return Result.success(carouselService.create(createDTO));
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/{id}")
    public Result<CarouselResponseDTO> update(@Parameter(description = "轮播ID") @PathVariable Long id,
                                              @Valid @RequestBody CarouselUpdateDTO updateDTO) {
        updateDTO.setId(id);
        log.info("更新轮播: {}", id);
        return Result.success(carouselService.update(updateDTO));
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "轮播ID") @PathVariable Long id) {
        log.info("删除轮播: {}", id);
        carouselService.delete(id);
        return Result.success();
    }

    @Operation(summary = "轮播图详情")
    @GetMapping("/{id}")
    public Result<CarouselResponseDTO> detail(@Parameter(description = "轮播ID") @PathVariable Long id) {
        return Result.success(carouselService.detail(id));
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping("/page")
    public Result<Page<CarouselResponseDTO>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "标题") @RequestParam(required = false) String title) {
        return Result.success(carouselService.page(current, size, status, title));
    }

    @Operation(summary = "获取启用中的轮播图列表")
    @GetMapping("/enabled")
    public Result<List<CarouselResponseDTO>> enabledList() {
        return Result.success(carouselService.listEnabled());
    }
}


