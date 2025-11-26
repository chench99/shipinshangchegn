package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.CartAddCommandDTO;
import org.example.springboot.DTO.command.CartUpdateCommandDTO;
import org.example.springboot.DTO.command.CartBatchDeleteCommandDTO;
import org.example.springboot.DTO.response.CartItemResponseDTO;
import org.example.springboot.DTO.response.CartListResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.CartService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车管理控制器
 * @author system
 */
@Tag(name = "购物车管理")
@RestController
@Slf4j
@RequestMapping("/cart")
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<CartItemResponseDTO> addToCart(
            @Valid @RequestBody CartAddCommandDTO commandDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}添加商品到购物车: {}", userId, commandDTO);
        
        CartItemResponseDTO response = cartService.addToCart(commandDTO, userId);
        return Result.success("添加到购物车成功", response);
    }

    /**
     * 获取用户购物车列表
     */
    @Operation(summary = "获取用户购物车列表")
    @GetMapping("/list")
    public Result<CartListResponseDTO> getCartList(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的购物车列表", userId);
        
        CartListResponseDTO response = cartService.getCartList(userId);
        return Result.success(response);
    }

    /**
     * 更新购物车商品数量
     */
    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/update/{cartId}")
    public Result<CartItemResponseDTO> updateCartQuantity(
            @Parameter(description = "购物车ID") @PathVariable Long cartId,
            @Valid @RequestBody CartUpdateCommandDTO commandDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}更新购物车{}的数量: {}", userId, cartId, commandDTO.getQuantity());
        
        CartItemResponseDTO response = cartService.updateCartQuantity(cartId, commandDTO, userId);
        return Result.success("更新数量成功", response);
    }

    /**
     * 删除购物车商品
     */
    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/delete/{cartId}")
    public Result<Void> deleteCartItem(
            @Parameter(description = "购物车ID") @PathVariable Long cartId,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}删除购物车商品: {}", userId, cartId);
        
        cartService.deleteCartItem(cartId, userId);
        return Result.success();
    }

    /**
     * 批量删除购物车商品
     */
    @Operation(summary = "批量删除购物车商品")
    @DeleteMapping("/batch-delete")
    public Result<Void> batchDeleteCartItems(
            @Valid @RequestBody CartBatchDeleteCommandDTO commandDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}批量删除购物车商品: {}", userId, commandDTO.getCartIds());
        
        cartService.batchDeleteCartItems(commandDTO.getCartIds(), userId);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}清空购物车", userId);
        
        cartService.clearCart(userId);
        return Result.success();
    }

    /**
     * 获取购物车商品数量统计
     */
    @Operation(summary = "获取购物车商品数量统计")
    @GetMapping("/count")
    public Result<Integer> getCartItemCount(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的购物车商品数量", userId);
        
        Integer count = cartService.getCartItemCount(userId);
        return Result.success(count);
    }
}
