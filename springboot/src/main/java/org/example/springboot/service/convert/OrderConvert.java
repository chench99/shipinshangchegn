package org.example.springboot.service.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.springboot.DTO.response.OrderItemResponseDTO;
import org.example.springboot.DTO.response.OrderResponseDTO;
import org.example.springboot.entity.Order;
import org.example.springboot.entity.OrderItem;
import org.example.springboot.enumClass.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单对象转换工具类
 * @author system
 */
public class OrderConvert {

    /**
     * 订单实体转为响应DTO
     */
    public static OrderResponseDTO convertToResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setOrderNo(order.getOrderNo());
        responseDTO.setUserId(order.getUserId());
        responseDTO.setAddressId(order.getAddressId());
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setTotalAmountYuan(convertToYuan(order.getTotalAmount()));
        responseDTO.setStatus(order.getStatus());
        responseDTO.setStatusDesc(getStatusDescription(order.getStatus()));
        responseDTO.setRemark(order.getRemark());
        responseDTO.setCreateTime(order.getCreateTime());
        responseDTO.setPaymentTime(order.getPaymentTime());
        responseDTO.setShipTime(order.getShipTime());
        responseDTO.setCompleteTime(order.getCompleteTime());
        responseDTO.setCancelTime(order.getCancelTime());
        return responseDTO;
    }

    /**
     * 订单项实体转为响应DTO
     */
    public static OrderItemResponseDTO convertToResponseDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemResponseDTO responseDTO = new OrderItemResponseDTO();
        responseDTO.setId(orderItem.getId());
        responseDTO.setOrderId(orderItem.getOrderId());
        responseDTO.setSnackId(orderItem.getSnackId());
        responseDTO.setQuantity(orderItem.getQuantity());
        responseDTO.setPrice(orderItem.getPrice());
        responseDTO.setPriceYuan(convertToYuan(orderItem.getPrice()));
        
        // 计算小计
        int subtotal = orderItem.getPrice() * orderItem.getQuantity();
        responseDTO.setSubtotal(subtotal);
        responseDTO.setSubtotalYuan(convertToYuan(subtotal));
        
        responseDTO.setSnackName(orderItem.getSnackName());
        responseDTO.setSnackImage(orderItem.getSnackImage());
        responseDTO.setCreateTime(orderItem.getCreateTime());
        return responseDTO;
    }

    /**
     * 订单实体列表转为响应DTO列表
     */
    public static List<OrderResponseDTO> convertToResponseDTOList(List<Order> orderList) {
        if (orderList == null) {
            return null;
        }
        return orderList.stream()
                .map(OrderConvert::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 订单项实体列表转为响应DTO列表
     */
    public static List<OrderItemResponseDTO> convertToOrderItemResponseDTOList(List<OrderItem> orderItemList) {
        if (orderItemList == null) {
            return null;
        }
        return orderItemList.stream()
                .map(OrderConvert::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页订单实体转为分页响应DTO
     */
    public static Page<OrderResponseDTO> convertToResponseDTOPage(Page<Order> orderPage) {
        if (orderPage == null) {
            return null;
        }
        Page<OrderResponseDTO> responsePage = new Page<>();
        responsePage.setCurrent(orderPage.getCurrent());
        responsePage.setSize(orderPage.getSize());
        responsePage.setTotal(orderPage.getTotal());
        responsePage.setRecords(convertToResponseDTOList(orderPage.getRecords()));
        return responsePage;
    }

    /**
     * 分转元
     */
    private static String convertToYuan(Integer fen) {
        if (fen == null) {
            return "0.00";
        }
        BigDecimal yuan = new BigDecimal(fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return yuan.toString();
    }

    /**
     * 获取状态描述
     */
    private static String getStatusDescription(String status) {
        if (status == null) {
            return "";
        }
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            return orderStatus.getDescription();
        } catch (IllegalArgumentException e) {
            return status;
        }
    }
}
