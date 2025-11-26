package org.example.springboot.service.convert;

import org.example.springboot.DTO.command.CartAddCommandDTO;
import org.example.springboot.DTO.response.CartItemResponseDTO;
import org.example.springboot.DTO.response.CartListResponseDTO;
import org.example.springboot.entity.Cart;
import org.example.springboot.entity.Snack;
import org.example.springboot.entity.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车数据转换工具类
 * @author system
 */
public class CartConvert {

    /**
     * 添加命令DTO转换为实体类
     */
    public static Cart addCommandToEntity(CartAddCommandDTO commandDTO, Long userId) {
        return Cart.builder()
                .userId(userId)
                .snackId(commandDTO.getSnackId())
                .quantity(commandDTO.getQuantity())
                .build();
    }

    /**
     * 购物车实体和零食实体转换为购物车项响应DTO
     */
    public static CartItemResponseDTO toCartItemResponse(Cart cart, Snack snack, String categoryName) {
        CartItemResponseDTO responseDTO = new CartItemResponseDTO();
        responseDTO.setId(cart.getId());
        responseDTO.setSnackId(snack.getId());
        responseDTO.setSnackName(snack.getName());
        responseDTO.setSnackDescription(snack.getDescription());
        responseDTO.setSnackImage(snack.getCoverImage());
        responseDTO.setQuantity(cart.getQuantity());
        responseDTO.setStock(snack.getStock());
        responseDTO.setStatus(snack.getStatus());
        responseDTO.setCategoryName(categoryName);
        
        // 价格转换：从分转换为元
        if (snack.getPrice() != null) {
            responseDTO.setPrice(new BigDecimal(snack.getPrice()).divide(new BigDecimal(100)));
        } else {
            responseDTO.setPrice(BigDecimal.ZERO);
        }
        
        // 计算小计和可购买状态
        responseDTO.calculateSubtotal();
        responseDTO.checkCanPurchase();
        
        return responseDTO;
    }

    /**
     * 购物车项列表转换为购物车列表响应DTO
     */
    public static CartListResponseDTO toCartListResponse(List<CartItemResponseDTO> items) {
        CartListResponseDTO responseDTO = new CartListResponseDTO();
        responseDTO.setItems(items);
        
        // 计算统计信息
        responseDTO.calculateStatistics();
        
        return responseDTO;
    }
}
