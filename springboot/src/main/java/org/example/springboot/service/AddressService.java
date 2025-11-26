package org.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.AddressCreateDTO;
import org.example.springboot.DTO.command.AddressUpdateDTO;
import org.example.springboot.DTO.response.AddressResponseDTO;
import org.example.springboot.entity.Address;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.AddressMapper;
import org.example.springboot.service.convert.AddressConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 收货地址管理服务
 * @author system
 */
@Slf4j
@Service
public class AddressService {

    @Resource
    private AddressMapper addressMapper;

    /**
     * 创建收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public AddressResponseDTO createAddress(AddressCreateDTO createDTO, Long userId) {
        log.info("用户{}创建收货地址: {}", userId, createDTO);

        // 验证用户ID
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 转换为实体对象
        Address address = AddressConvert.convertToEntity(createDTO, userId);

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearOtherDefaultAddress(userId);
        }

        // 保存地址
        int result = addressMapper.insert(address);
        if (result <= 0) {
            throw new BusinessException("创建收货地址失败");
        }

        log.info("收货地址创建成功: addressId={}", address.getId());
        return AddressConvert.convertToResponseDTO(address);
    }

    /**
     * 更新收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public AddressResponseDTO updateAddress(Long addressId, AddressUpdateDTO updateDTO, Long userId) {
        log.info("用户{}更新收货地址{}: {}", userId, addressId, updateDTO);

        // 验证地址是否存在且属于当前用户
        Address existingAddress = validateAddressOwnership(addressId, userId);

        // 转换为实体对象
        Address address = AddressConvert.convertToEntity(updateDTO, userId, addressId);

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearOtherDefaultAddress(userId);
        }

        // 更新地址
        int result = addressMapper.updateById(address);
        if (result <= 0) {
            throw new BusinessException("更新收货地址失败");
        }

        log.info("收货地址更新成功: addressId={}", addressId);
        // 重新查询并返回
        Address updatedAddress = addressMapper.selectById(addressId);
        return AddressConvert.convertToResponseDTO(updatedAddress);
    }

    /**
     * 删除收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long addressId, Long userId) {
        log.info("用户{}删除收货地址: addressId={}", userId, addressId);

        // 验证地址是否存在且属于当前用户
        validateAddressOwnership(addressId, userId);

        // 删除地址
        int result = addressMapper.deleteById(addressId);
        if (result <= 0) {
            throw new BusinessException("删除收货地址失败");
        }

        log.info("收货地址删除成功: addressId={}", addressId);
    }

    /**
     * 设置默认地址
     */
    @Transactional(rollbackFor = Exception.class)
    public AddressResponseDTO setDefaultAddress(Long addressId, Long userId) {
        log.info("用户{}设置默认地址: addressId={}", userId, addressId);

        // 验证地址是否存在且属于当前用户
        validateAddressOwnership(addressId, userId);

        // 先取消其他默认地址
        clearOtherDefaultAddress(userId);

        // 设置当前地址为默认
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getId, addressId)
                     .eq(Address::getUserId, userId)
                     .set(Address::getIsDefault, true);

        int result = addressMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("设置默认地址失败");
        }

        log.info("默认地址设置成功: addressId={}", addressId);
        // 重新查询并返回
        Address updatedAddress = addressMapper.selectById(addressId);
        return AddressConvert.convertToResponseDTO(updatedAddress);
    }

    /**
     * 获取用户地址列表
     */
    public List<AddressResponseDTO> getUserAddressList(Long userId) {
        log.info("获取用户{}的地址列表", userId);

        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                    .orderByDesc(Address::getIsDefault)
                    .orderByDesc(Address::getCreateTime);

        List<Address> addressList = addressMapper.selectList(queryWrapper);
        return AddressConvert.convertToResponseDTOList(addressList);
    }

    /**
     * 根据ID获取地址详情
     */
    public AddressResponseDTO getAddressById(Long addressId, Long userId) {
        log.info("获取地址详情: addressId={}, userId={}", addressId, userId);

        Address address = validateAddressOwnership(addressId, userId);
        return AddressConvert.convertToResponseDTO(address);
    }

    /**
     * 获取用户默认地址
     */
    public AddressResponseDTO getDefaultAddress(Long userId) {
        log.info("获取用户{}的默认地址", userId);

        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                    .eq(Address::getIsDefault, true)
                    .orderByDesc(Address::getCreateTime)
                    .last("LIMIT 1");

        Address defaultAddress = addressMapper.selectOne(queryWrapper);
        return AddressConvert.convertToResponseDTO(defaultAddress);
    }

    /**
     * 验证地址归属权限
     */
    private Address validateAddressOwnership(Long addressId, Long userId) {
        if (addressId == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该收货地址");
        }

        return address;
    }

    /**
     * 清除用户其他默认地址
     */
    private void clearOtherDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId)
                     .eq(Address::getIsDefault, true)
                     .set(Address::getIsDefault, false);
        
        addressMapper.update(null, updateWrapper);
        log.info("已清除用户{}的其他默认地址", userId);
    }
}
