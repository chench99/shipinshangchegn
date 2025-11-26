package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.DTO.FileUploadDTO;
import org.example.springboot.DTO.command.ReviewCreateDTO;
import org.example.springboot.DTO.response.ReviewResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.enumClass.FileBusinessTypeEnum;
import org.example.springboot.service.FileService;
import org.example.springboot.service.ReviewService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评价管理")
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @Resource
    private FileService fileService;

    @Operation(summary = "创建评价")
    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody ReviewCreateDTO dto) {
        Long id = reviewService.createReview(dto.getSnackId(), dto.getRating(), dto.getContent());
        return Result.success(id);
    }

    @Operation(summary = "校验是否可评价")
    @GetMapping("/can-review")
    public Result<Boolean> canReview(
            @Parameter(description = "零食ID") @RequestParam Long snackId) {
        try {
            Long userId = org.example.springboot.util.JwtTokenUtils.getCurrentUserId();
            boolean can = reviewService.canUserReview(snackId, userId);
            return Result.success(can);
        } catch (Exception e) {
            // 未登录或Token解析异常，返回不可评价
            return Result.success(false);
        }
    }

    @Operation(summary = "分页查询评价")
    @GetMapping("/page")
    public Result<Page<ReviewResponseDTO>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "零食ID") @RequestParam(required = false) Long snackId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "评分") @RequestParam(required = false) Integer rating) {
        Page<ReviewResponseDTO> page = reviewService.getReviewPage(snackId, userId, rating, current, size);
        return Result.success(page);
    }

    @Operation(summary = "确认评价图片（策略A第二阶段）")
    @PutMapping("/{reviewId}/confirm-image/{tempFileId}")
    public Result<?> confirmImage(
            @PathVariable Long reviewId,
            @PathVariable Long tempFileId) {
        FileUploadDTO uploadDTO = new FileUploadDTO();
        uploadDTO.setBusinessType(FileBusinessTypeEnum.COMMENT_IMAGE.getCode());
        uploadDTO.setBusinessId(String.valueOf(reviewId));
        uploadDTO.setBusinessField("images");
        fileService.confirmTempFile(tempFileId, uploadDTO);
        return Result.success();
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(reviewService.deleteReview(id));
    }
}


