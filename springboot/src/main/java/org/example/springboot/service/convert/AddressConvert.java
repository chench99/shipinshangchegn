package org.example.springboot.service.convert;

import org.example.springboot.DTO.command.AddressCreateDTO;
import org.example.springboot.DTO.command.AddressUpdateDTO;
import org.example.springboot.DTO.response.AddressResponseDTO;
import org.example.springboot.entity.Address;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址对象转换工具类
 * @author system
 */
public class AddressConvert {

    /**
     * 创建DTO转为实体
     */
    public static Address convertToEntity(AddressCreateDTO createDTO, Long userId) {
        if (createDTO == null) {
            return null;
        }
        return Address.builder()
                .userId(userId)
                .consigneeName(createDTO.getConsigneeName())
                .phone(createDTO.getPhone())
                .province(createDTO.getProvince())
                .city(createDTO.getCity())
                .district(createDTO.getDistrict())
                .detailedAddress(createDTO.getDetailedAddress())
                .isDefault(createDTO.getIsDefault() != null ? createDTO.getIsDefault() : false)
                .build();
    }

    /**
     * 更新DTO转为实体
     */
    public static Address convertToEntity(AddressUpdateDTO updateDTO, Long userId, Long addressId) {
        if (updateDTO == null) {
            return null;
        }
        return Address.builder()
                .id(addressId)
                .userId(userId)
                .consigneeName(updateDTO.getConsigneeName())
                .phone(updateDTO.getPhone())
                .province(updateDTO.getProvince())
                .city(updateDTO.getCity())
                .district(updateDTO.getDistrict())
                .detailedAddress(updateDTO.getDetailedAddress())
                .isDefault(updateDTO.getIsDefault() != null ? updateDTO.getIsDefault() : false)
                .build();
    }

    /**
     * 实体转为响应DTO
     */
    public static AddressResponseDTO convertToResponseDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponseDTO responseDTO = new AddressResponseDTO();
        responseDTO.setId(address.getId());
        responseDTO.setUserId(address.getUserId());
        responseDTO.setConsigneeName(address.getConsigneeName());
        responseDTO.setPhone(address.getPhone());
        responseDTO.setProvince(address.getProvince());
        responseDTO.setCity(address.getCity());
        responseDTO.setDistrict(address.getDistrict());
        responseDTO.setDetailedAddress(address.getDetailedAddress());
        responseDTO.setFullAddress(buildFullAddress(address));
        responseDTO.setIsDefault(address.getIsDefault());
        responseDTO.setCreateTime(address.getCreateTime());
        responseDTO.setUpdateTime(address.getUpdateTime());
        return responseDTO;
    }

    /**
     * 实体列表转为响应DTO列表
     */
    public static List<AddressResponseDTO> convertToResponseDTOList(List<Address> addressList) {
        if (addressList == null) {
            return null;
        }
        return addressList.stream()
                .map(AddressConvert::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 构建完整地址
     */
    private static String buildFullAddress(Address address) {
        StringBuilder fullAddress = new StringBuilder();
        if (address.getProvince() != null && !address.getProvince().trim().isEmpty()) {
            fullAddress.append(address.getProvince());
        }
        if (address.getCity() != null && !address.getCity().trim().isEmpty()) {
            fullAddress.append(address.getCity());
        }
        if (address.getDistrict() != null && !address.getDistrict().trim().isEmpty()) {
            fullAddress.append(address.getDistrict());
        }
        if (address.getDetailedAddress() != null && !address.getDetailedAddress().trim().isEmpty()) {
            fullAddress.append(address.getDetailedAddress());
        }
        return fullAddress.toString();
    }
}
