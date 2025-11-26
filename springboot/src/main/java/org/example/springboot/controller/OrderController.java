package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.OrderCreateDTO;
import org.example.springboot.DTO.response.OrderResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.OrderService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器
 * @author system
 */
@Tag(name = "订单管理")
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderCreateDTO createDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}创建订单", userId);
        
        OrderResponseDTO response = orderService.createOrder(createDTO, userId);
        return Result.success("创建订单成功", response);
    }

    /**
     * 订单支付
     */
    @Operation(summary = "订单支付")
    @PutMapping("/{id}/pay")
    public Result<OrderResponseDTO> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}支付订单{}", userId, id);
        
        OrderResponseDTO response = orderService.payOrder(id, userId);
        return Result.success("支付成功", response);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<OrderResponseDTO> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}取消订单{}", userId, id);
        
        OrderResponseDTO response = orderService.cancelOrder(id, userId);
        return Result.success("取消订单成功", response);
    }

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货")
    @PutMapping("/{id}/complete")
    public Result<OrderResponseDTO> completeOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}确认收货{}", userId, id);
        
        OrderResponseDTO response = orderService.completeOrder(id, userId);
        return Result.success("确认收货成功", response);
    }

    /**
     * 获取用户订单列表
     */
    @Operation(summary = "获取用户订单列表")
    @GetMapping("/page")
    public Result<Page<OrderResponseDTO>> getUserOrderPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的订单列表: current={}, size={}, status={}", userId, current, size, status);
        
        Page<OrderResponseDTO> response = orderService.getUserOrderPage(current, size, status, userId);
        return Result.success(response);
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderResponseDTO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取订单详情: orderId={}", id);
        
        OrderResponseDTO response = orderService.getOrderDetail(id, userId);
        return Result.success(response);
    }

    /**
     * 管理员获取所有订单列表
     */
    @Operation(summary = "管理员获取所有订单列表")
    @GetMapping("/admin/page")
    public Result<Page<OrderResponseDTO>> getAllOrderPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status) {
        log.info("管理员获取订单列表: current={}, size={}, orderNo={}, status={}", current, size, orderNo, status);
        
        Page<OrderResponseDTO> response = orderService.getAllOrderPage(current, size, orderNo, status);
        return Result.success(response);
    }

    /**
     * 管理员订单发货
     */
    @Operation(summary = "管理员订单发货")
    @PutMapping("/admin/{id}/ship")
    public Result<OrderResponseDTO> shipOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("管理员发货订单: orderId={}", id);
        
        OrderResponseDTO response = orderService.shipOrder(id);
        return Result.success("订单发货成功", response);
    }

    /**
     * 管理员获取订单详情
     */
    @Operation(summary = "管理员获取订单详情")
    @GetMapping("/admin/{id}")
    public Result<OrderResponseDTO> getAdminOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("管理员获取订单详情: orderId={}", id);
        
        OrderResponseDTO response = orderService.getOrderDetail(id, null);
        return Result.success(response);
    }
}
