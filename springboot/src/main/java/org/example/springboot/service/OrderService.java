package org.example.springboot.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.OrderCreateDTO;
import org.example.springboot.DTO.response.AddressResponseDTO;
import org.example.springboot.DTO.response.OrderResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.enumClass.OrderStatus;
import org.example.springboot.enumClass.SnackStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.example.springboot.service.convert.OrderConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单管理服务
 * @author system
 */
@Slf4j
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private SnackMapper snackMapper;

    @Resource
    private AddressService addressService;

    private static final String ORDER_TYPE_CART = "CART_ORDER";
    private static final String ORDER_TYPE_DIRECT = "DIRECT_ORDER";

    /**
     * 创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO createOrder(OrderCreateDTO createDTO, Long userId) {
        log.info("用户{}创建订单: {}", userId, createDTO);

        // 1. 验证收货地址
        AddressResponseDTO address = addressService.getAddressById(createDTO.getAddressId(), userId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 2. 根据订单类型处理商品信息
        List<OrderItemInfo> orderItemInfos;
        if (ORDER_TYPE_CART.equals(createDTO.getOrderType())) {
            orderItemInfos = processCartOrder(createDTO.getCartItemIds(), userId);
        } else if (ORDER_TYPE_DIRECT.equals(createDTO.getOrderType())) {
            orderItemInfos = processDirectOrder(createDTO.getDirectOrderItem(), userId);
        } else {
            throw new BusinessException("无效的订单类型");
        }

        // 3. 计算订单总金额
        int totalAmount = orderItemInfos.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        // 4. 生成订单号
        String orderNo = generateOrderNo();

        // 5. 创建订单主体
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .addressId(createDTO.getAddressId())
                .totalAmount(totalAmount)
                .status(OrderStatus.UNPAID.name())
                .remark(createDTO.getRemark())
                .createTime(LocalDateTime.now())
                .build();

        int orderResult = orderMapper.insert(order);
        if (orderResult <= 0) {
            throw new BusinessException("创建订单失败");
        }

        // 6. 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .snackId(itemInfo.getSnackId())
                    .quantity(itemInfo.getQuantity())
                    .price(itemInfo.getPrice())
                    .snackName(itemInfo.getSnackName())
                    .snackImage(itemInfo.getSnackImage())
                    .createTime(LocalDateTime.now())
                    .build();
            orderItems.add(orderItem);
        }

        // 批量插入订单项
        for (OrderItem orderItem : orderItems) {
            orderItemMapper.insert(orderItem);
        }

        // 7. 如果是购物车下单，清空对应的购物车项
        if (ORDER_TYPE_CART.equals(createDTO.getOrderType())) {
            clearCartItems(createDTO.getCartItemIds(), userId);
        }

        // 8. 扣减库存
        updateSnackStock(orderItemInfos);

        log.info("订单创建成功: orderId={}, orderNo={}", order.getId(), orderNo);

        // 9. 返回订单详情
        return getOrderDetail(order.getId(), userId);
    }

    /**
     * 订单支付
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO payOrder(Long orderId, Long userId) {
        log.info("用户{}支付订单: orderId={}", userId, orderId);

        // 1. 验证订单
        Order order = validateOrderOwnership(orderId, userId);
        
        // 2. 检查订单状态
        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
        if (!currentStatus.canPay()) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 3. 更新订单状态
        order.setStatus(OrderStatus.PAID.name());
        order.setPaymentTime(LocalDateTime.now());
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("支付订单失败");
        }

        log.info("订单支付成功: orderId={}", orderId);
        return getOrderDetail(orderId, userId);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO cancelOrder(Long orderId, Long userId) {
        log.info("用户{}取消订单: orderId={}", userId, orderId);

        // 1. 验证订单
        Order order = validateOrderOwnership(orderId, userId);
        
        // 2. 检查订单状态
        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
        if (!currentStatus.canCancel()) {
            throw new BusinessException("订单状态不允许取消");
        }

        // 3. 恢复库存
        restoreSnackStock(orderId);

        // 4. 更新订单状态
        order.setStatus(OrderStatus.CANCELLED.name());
        order.setCancelTime(LocalDateTime.now());
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("取消订单失败");
        }

        log.info("订单取消成功: orderId={}", orderId);
        return getOrderDetail(orderId, userId);
    }

    /**
     * 确认收货
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO completeOrder(Long orderId, Long userId) {
        log.info("用户{}确认收货: orderId={}", userId, orderId);

        // 1. 验证订单
        Order order = validateOrderOwnership(orderId, userId);
        
        // 2. 检查订单状态
        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
        if (!currentStatus.canComplete()) {
            throw new BusinessException("订单状态不允许确认收货");
        }

        // 3. 更新订单状态
        order.setStatus(OrderStatus.COMPLETED.name());
        order.setCompleteTime(LocalDateTime.now());
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("确认收货失败");
        }

        log.info("订单确认收货成功: orderId={}", orderId);
        return getOrderDetail(orderId, userId);
    }

    /**
     * 订单发货（管理员操作）
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO shipOrder(Long orderId) {
        log.info("管理员发货订单: orderId={}", orderId);

        // 1. 验证订单存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 检查订单状态
        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
        if (!currentStatus.canShip()) {
            throw new BusinessException("订单状态不允许发货");
        }

        // 3. 更新订单状态
        order.setStatus(OrderStatus.SHIPPED.name());
        order.setShipTime(LocalDateTime.now());
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("订单发货失败");
        }

        log.info("订单发货成功: orderId={}", orderId);
        return getOrderDetail(orderId, null);
    }

    /**
     * 获取用户订单列表
     */
    public Page<OrderResponseDTO> getUserOrderPage(Long current, Long size, String status, Long userId) {
        log.info("获取用户{}的订单列表: current={}, size={}, status={}", userId, current, size, status);

        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        
        if (StrUtil.isNotBlank(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(page, queryWrapper);
        return OrderConvert.convertToResponseDTOPage(orderPage);
    }

    /**
     * 获取所有订单列表（管理员）
     */
    public Page<OrderResponseDTO> getAllOrderPage(Long current, Long size, String orderNo, String status) {
        log.info("管理员获取订单列表: current={}, size={}, orderNo={}, status={}", current, size, orderNo, status);

        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(orderNo)) {
            queryWrapper.like(Order::getOrderNo, orderNo);
        }
        
        if (StrUtil.isNotBlank(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(page, queryWrapper);
        return OrderConvert.convertToResponseDTOPage(orderPage);
    }

    /**
     * 获取订单详情
     */
    public OrderResponseDTO getOrderDetail(Long orderId, Long userId) {
        log.info("获取订单详情: orderId={}, userId={}", orderId, userId);

        // 1. 验证订单（如果userId不为空）
        Order order;
        if (userId != null) {
            order = validateOrderOwnership(orderId, userId);
        } else {
            order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
        }

        // 2. 获取订单项
        LambdaQueryWrapper<OrderItem> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemQueryWrapper);

        // 3. 获取地址信息
        AddressResponseDTO address = addressService.getAddressById(order.getAddressId(), order.getUserId());

        // 4. 构建响应
        OrderResponseDTO response = OrderConvert.convertToResponseDTO(order);
        response.setOrderItems(OrderConvert.convertToOrderItemResponseDTOList(orderItems));
        response.setAddress(address);

        return response;
    }

    /**
     * 处理购物车订单
     */
    private List<OrderItemInfo> processCartOrder(List<Long> cartItemIds, Long userId) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new BusinessException("购物车商品不能为空");
        }

        // 1. 查询购物车项
        LambdaQueryWrapper<Cart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.eq(Cart::getUserId, userId)
                        .in(Cart::getId, cartItemIds);
        List<Cart> cartItems = cartMapper.selectList(cartQueryWrapper);

        if (cartItems.size() != cartItemIds.size()) {
            throw new BusinessException("部分购物车商品不存在");
        }

        // 2. 获取商品信息并验证
        List<Long> snackIds = cartItems.stream().map(Cart::getSnackId).collect(Collectors.toList());
        LambdaQueryWrapper<Snack> snackQueryWrapper = new LambdaQueryWrapper<>();
        snackQueryWrapper.in(Snack::getId, snackIds);
        List<Snack> snacks = snackMapper.selectList(snackQueryWrapper);
        
        Map<Long, Snack> snackMap = snacks.stream().collect(Collectors.toMap(Snack::getId, snack -> snack));

        // 3. 构建订单项信息
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            Snack snack = snackMap.get(cartItem.getSnackId());
            if (snack == null) {
                throw new BusinessException("商品不存在: " + cartItem.getSnackId());
            }
            
            // 验证商品状态和库存
            validateSnackForOrder(snack, cartItem.getQuantity());
            
            OrderItemInfo itemInfo = new OrderItemInfo();
            itemInfo.setSnackId(snack.getId());
            itemInfo.setQuantity(cartItem.getQuantity());
            itemInfo.setPrice(snack.getPrice());
            itemInfo.setSnackName(snack.getName());
            itemInfo.setSnackImage(snack.getCoverImage());
            orderItemInfos.add(itemInfo);
        }

        return orderItemInfos;
    }

    /**
     * 处理直接下单
     */
    private List<OrderItemInfo> processDirectOrder(OrderCreateDTO.DirectOrderItem directItem, Long userId) {
        if (directItem == null) {
            throw new BusinessException("商品信息不能为空");
        }

        // 1. 查询商品信息
        Snack snack = snackMapper.selectById(directItem.getSnackId());
        if (snack == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 验证商品状态和库存
        validateSnackForOrder(snack, directItem.getQuantity());

        // 3. 构建订单项信息
        OrderItemInfo itemInfo = new OrderItemInfo();
        itemInfo.setSnackId(snack.getId());
        itemInfo.setQuantity(directItem.getQuantity());
        itemInfo.setPrice(snack.getPrice());
        itemInfo.setSnackName(snack.getName());
        itemInfo.setSnackImage(snack.getCoverImage());

        return List.of(itemInfo);
    }

    /**
     * 验证商品是否可以下单
     */
    private void validateSnackForOrder(Snack snack, Integer quantity) {
        if (!SnackStatus.ON_SALE.name().equals(snack.getStatus())) {
            throw new BusinessException("商品已下架: " + snack.getName());
        }
        
        if (snack.getStock() < quantity) {
            throw new BusinessException("商品库存不足: " + snack.getName());
        }
    }

    /**
     * 清空购物车项
     */
    private void clearCartItems(List<Long> cartItemIds, Long userId) {
        LambdaQueryWrapper<Cart> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Cart::getUserId, userId)
                     .in(Cart::getId, cartItemIds);
        cartMapper.delete(deleteWrapper);
        log.info("已清空用户{}的购物车项: {}", userId, cartItemIds);
    }

    /**
     * 扣减库存
     */
    private void updateSnackStock(List<OrderItemInfo> orderItemInfos) {
        for (OrderItemInfo itemInfo : orderItemInfos) {
            LambdaUpdateWrapper<Snack> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Snack::getId, itemInfo.getSnackId())
                         .setSql("stock = stock - " + itemInfo.getQuantity());
            
            int result = snackMapper.update(null, updateWrapper);
            if (result <= 0) {
                throw new BusinessException("扣减库存失败: " + itemInfo.getSnackName());
            }
            log.info("扣减商品库存: snackId={}, quantity={}", itemInfo.getSnackId(), itemInfo.getQuantity());
        }
    }

    /**
     * 恢复库存
     */
    private void restoreSnackStock(Long orderId) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);

        for (OrderItem orderItem : orderItems) {
            LambdaUpdateWrapper<Snack> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Snack::getId, orderItem.getSnackId())
                         .setSql("stock = stock + " + orderItem.getQuantity());
            
            snackMapper.update(null, updateWrapper);
            log.info("恢复商品库存: snackId={}, quantity={}", orderItem.getSnackId(), orderItem.getQuantity());
        }
    }

    /**
     * 验证订单归属权限
     */
    private Order validateOrderOwnership(Long orderId, Long userId) {
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该订单");
        }

        return order;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 格式：yyyyMMddHHmmss + 6位随机数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomId = IdUtil.fastSimpleUUID().substring(0, 6);
        return "ORDER" + timestamp + randomId.toUpperCase();
    }

    /**
     * 订单项信息内部类
     */
    private static class OrderItemInfo {
        private Long snackId;
        private Integer quantity;
        private Integer price;
        private String snackName;
        private String snackImage;

        // getters and setters
        public Long getSnackId() { return snackId; }
        public void setSnackId(Long snackId) { this.snackId = snackId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        
        public String getSnackName() { return snackName; }
        public void setSnackName(String snackName) { this.snackName = snackName; }
        
        public String getSnackImage() { return snackImage; }
        public void setSnackImage(String snackImage) { this.snackImage = snackImage; }
    }
}
