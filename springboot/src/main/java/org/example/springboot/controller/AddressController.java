package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.AddressCreateDTO;
import org.example.springboot.DTO.command.AddressUpdateDTO;
import org.example.springboot.DTO.response.AddressResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.service.AddressService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址管理控制器
 * @author system
 */
@Tag(name = "收货地址管理")
@RestController
@Slf4j
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    /**
     * 创建收货地址
     */
    @Operation(summary = "创建收货地址")
    @PostMapping
    public Result<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressCreateDTO createDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}创建收货地址", userId);
        
        AddressResponseDTO response = addressService.createAddress(createDTO, userId);
        return Result.success("创建地址成功", response);
    }

    /**
     * 更新收货地址
     */
    @Operation(summary = "更新收货地址")
    @PutMapping("/{id}")
    public Result<AddressResponseDTO> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Valid @RequestBody AddressUpdateDTO updateDTO,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}更新收货地址{}", userId, id);
        
        AddressResponseDTO response = addressService.updateAddress(id, updateDTO, userId);
        return Result.success("更新地址成功", response);
    }

    /**
     * 删除收货地址
     */
    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}删除收货地址{}", userId, id);
        
        addressService.deleteAddress(id, userId);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @Operation(summary = "设置默认地址")
    @PutMapping("/{id}/default")
    public Result<AddressResponseDTO> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("用户{}设置默认地址{}", userId, id);
        
        AddressResponseDTO response = addressService.setDefaultAddress(id, userId);
        return Result.success("设置默认地址成功", response);
    }

    /**
     * 获取用户地址列表
     */
    @Operation(summary = "获取用户地址列表")
    @GetMapping
    public Result<List<AddressResponseDTO>> getUserAddressList(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的地址列表", userId);
        
        List<AddressResponseDTO> response = addressService.getUserAddressList(userId);
        return Result.success(response);
    }

    /**
     * 根据ID获取地址详情
     */
    @Operation(summary = "根据ID获取地址详情")
    @GetMapping("/{id}")
    public Result<AddressResponseDTO> getAddressById(
            @Parameter(description = "地址ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取地址详情: addressId={}", id);
        
        AddressResponseDTO response = addressService.getAddressById(id, userId);
        return Result.success(response);
    }

    /**
     * 获取用户默认地址
     */
    @Operation(summary = "获取用户默认地址")
    @GetMapping("/default")
    public Result<AddressResponseDTO> getDefaultAddress(HttpServletRequest request) {
        Long userId = JwtTokenUtils.getCurrentUserIdFromRequest(request);
        log.info("获取用户{}的默认地址", userId);
        
        AddressResponseDTO response = addressService.getDefaultAddress(userId);
        return Result.success(response);
    }
}
