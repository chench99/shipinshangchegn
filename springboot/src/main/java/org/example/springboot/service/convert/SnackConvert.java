package org.example.springboot.service.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springboot.DTO.command.SnackCreateDTO;
import org.example.springboot.DTO.command.SnackUpdateDTO;
import org.example.springboot.DTO.response.SnackListResponseDTO;
import org.example.springboot.DTO.response.SnackResponseDTO;
import org.example.springboot.entity.Snack;
import org.example.springboot.enumClass.SnackStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 零食转换类
 * @author system
 */
public class SnackConvert {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建命令DTO转换为Snack实体
     * @param createDTO 创建命令DTO
     * @return Snack实体
     */
    public static Snack createCommandToEntity(SnackCreateDTO createDTO) {
        return Snack.builder()
                .categoryId(createDTO.getCategoryId())
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .price(createDTO.getPrice().multiply(BigDecimal.valueOf(100)).intValue()) // 元转分
                .stock(createDTO.getStock())
                .coverImage(createDTO.getCoverImage())
                .detailImages(listToJsonString(createDTO.getDetailImages()))
                .status(createDTO.getStatus() != null ? createDTO.getStatus() : SnackStatus.ON_SALE.getCode())
                .salesCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为Snack实体（用于更新操作）
     * @param updateDTO 更新命令DTO
     * @return Snack实体
     */
    public static Snack updateCommandToEntity(SnackUpdateDTO updateDTO) {
        return Snack.builder()
                .id(updateDTO.getId())
                .categoryId(updateDTO.getCategoryId())
                .name(updateDTO.getName())
                .description(updateDTO.getDescription())
                .price(updateDTO.getPrice().multiply(BigDecimal.valueOf(100)).intValue()) // 元转分
                .stock(updateDTO.getStock())
                .coverImage(updateDTO.getCoverImage())
                .detailImages(listToJsonString(updateDTO.getDetailImages()))
                .status(updateDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * Snack实体转换为响应DTO
     * @param snack Snack实体
     * @param categoryName 分类名称
     * @return 零食响应DTO
     */
    public static SnackResponseDTO entityToResponse(Snack snack, String categoryName) {
        return SnackResponseDTO.builder()
                .id(snack.getId())
                .categoryId(snack.getCategoryId())
                .categoryName(categoryName)
                .name(snack.getName())
                .description(snack.getDescription())
                .price(snack.getPriceInYuan())
                .stock(snack.getStock())
                .coverImage(snack.getCoverImage())
                .detailImages(jsonStringToList(snack.getDetailImages()))
                .status(snack.getStatus())
                .statusDisplayName(snack.getStatusDisplayName())
                .salesCount(snack.getSalesCount())
                .createTime(snack.getCreateTime())
                .updateTime(snack.getUpdateTime())
                .onSale(snack.isOnSale())
                .hasStock(snack.hasStock())
                .canPurchase(snack.canPurchase())
                .build();
    }

    /**
     * Snack实体转换为列表响应DTO
     * @param snack Snack实体
     * @param categoryName 分类名称
     * @return 零食列表响应DTO
     */
    public static SnackListResponseDTO entityToListResponse(Snack snack, String categoryName) {
        return SnackListResponseDTO.builder()
                .id(snack.getId())
                .categoryId(snack.getCategoryId())
                .categoryName(categoryName)
                .name(snack.getName())
                .description(snack.getDescription())
                .price(snack.getPriceInYuan())
                .stock(snack.getStock())
                .coverImage(snack.getCoverImage())
                .status(snack.getStatus())
                .statusDisplayName(snack.getStatusDisplayName())
                .salesCount(snack.getSalesCount())
                .createTime(snack.getCreateTime())
                .updateTime(snack.getUpdateTime())
                .onSale(snack.isOnSale())
                .hasStock(snack.hasStock())
                .canPurchase(snack.canPurchase())
                .build();
    }

    /**
     * List转换为JSON字符串
     * @param list 列表
     * @return JSON字符串
     */
    public static String listToJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * JSON字符串转换为List
     * @param jsonString JSON字符串
     * @return 列表
     */
    public static List<String> jsonStringToList(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
