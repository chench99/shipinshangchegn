package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.springboot.entity.Cart;
import org.example.springboot.entity.Snack;
import org.example.springboot.entity.Category;
import org.example.springboot.mapper.CartMapper;
import org.example.springboot.mapper.SnackMapper;
import org.example.springboot.mapper.CategoryMapper;
import org.example.springboot.DTO.command.CartAddCommandDTO;
import org.example.springboot.DTO.command.CartUpdateCommandDTO;
import org.example.springboot.DTO.response.CartItemResponseDTO;
import org.example.springboot.DTO.response.CartListResponseDTO;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.service.convert.CartConvert;

/**
 * 购物车业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private SnackMapper snackMapper;

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 添加商品到购物车
     * @param commandDTO 添加命令
     * @param userId 用户ID
     * @return 购物车项响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CartItemResponseDTO addToCart(CartAddCommandDTO commandDTO, Long userId) {
        log.info("用户{}添加商品{}到购物车，数量: {}", userId, commandDTO.getSnackId(), commandDTO.getQuantity());
        
        // 1. 校验零食是否存在且可购买
        Snack snack = validateSnackForCart(commandDTO.getSnackId(), commandDTO.getQuantity());
        
        // 2. 检查购物车中是否已存在该商品
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                   .eq(Cart::getSnackId, commandDTO.getSnackId());
        Cart existingCart = cartMapper.selectOne(queryWrapper);
        
        if (existingCart != null) {
            // 更新现有购物车项的数量
            int newQuantity = existingCart.getQuantity() + commandDTO.getQuantity();
            
            // 再次校验总数量是否超过库存
            if (newQuantity > snack.getStock()) {
                throw new BusinessException("商品库存不足，当前库存: " + snack.getStock() + 
                                          "，购物车已有: " + existingCart.getQuantity() + 
                                          "，尝试添加: " + commandDTO.getQuantity());
            }
            
            existingCart.setQuantity(newQuantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existingCart);
            
            log.info("更新购物车商品数量成功，购物车ID: {}, 新数量: {}", existingCart.getId(), newQuantity);
            return buildCartItemResponse(existingCart, snack);
        } else {
            // 创建新的购物车项
            Cart newCart = CartConvert.addCommandToEntity(commandDTO, userId);
            newCart.setCreateTime(LocalDateTime.now());
            newCart.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(newCart);
            
            log.info("添加商品到购物车成功，购物车ID: {}", newCart.getId());
            return buildCartItemResponse(newCart, snack);
        }
    }

    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车列表响应DTO
     */
    public CartListResponseDTO getCartList(Long userId) {
        log.info("获取用户{}的购物车列表", userId);
        
        // 1. 查询用户的购物车项
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                   .orderByDesc(Cart::getUpdateTime);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        
        if (cartList.isEmpty()) {
            log.info("用户{}的购物车为空", userId);
            return CartConvert.toCartListResponse(List.of());
        }
        
        // 2. 获取购物车中所有零食的ID
        List<Long> snackIds = cartList.stream()
                .map(Cart::getSnackId)
                .collect(Collectors.toList());
        
        // 3. 批量查询零食信息
        LambdaQueryWrapper<Snack> snackQueryWrapper = new LambdaQueryWrapper<>();
        snackQueryWrapper.in(Snack::getId, snackIds);
        List<Snack> snackList = snackMapper.selectList(snackQueryWrapper);
        Map<Long, Snack> snackMap = snackList.stream()
                .collect(Collectors.toMap(Snack::getId, snack -> snack));
        
        // 4. 批量查询分类信息
        List<Long> categoryIds = snackList.stream()
                .map(Snack::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        
        final Map<Long, String> categoryMap;
        if (!categoryIds.isEmpty()) {
            LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
            categoryQueryWrapper.in(Category::getId, categoryIds);
            List<Category> categoryList = categoryMapper.selectList(categoryQueryWrapper);
            categoryMap = categoryList.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        } else {
            categoryMap = Map.of();
        }
        
        // 5. 构建响应DTO列表
        List<CartItemResponseDTO> itemList = cartList.stream()
                .map(cart -> {
                    Snack snack = snackMap.get(cart.getSnackId());
                    if (snack == null) {
                        log.warn("购物车中的商品不存在，购物车ID: {}, 商品ID: {}", cart.getId(), cart.getSnackId());
                        // 商品不存在，可能已被删除，这里可以选择删除购物车项或返回null
                        return null;
                    }
                    String categoryName = categoryMap.get(snack.getCategoryId());
                    return CartConvert.toCartItemResponse(cart, snack, categoryName);
                })
                .filter(item -> item != null) // 过滤掉null项（商品不存在的情况）
                .collect(Collectors.toList());
        
        log.info("用户{}的购物车包含{}个商品项", userId, itemList.size());
        return CartConvert.toCartListResponse(itemList);
    }

    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param commandDTO 更新命令
     * @param userId 用户ID
     * @return 购物车项响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CartItemResponseDTO updateCartQuantity(Long cartId, CartUpdateCommandDTO commandDTO, Long userId) {
        log.info("用户{}更新购物车{}的数量为{}", userId, cartId, commandDTO.getQuantity());
        
        // 1. 查询并验证购物车项
        Cart cart = getCartByIdAndUserId(cartId, userId);
        
        // 2. 校验零食库存
        Snack snack = validateSnackForCart(cart.getSnackId(), commandDTO.getQuantity());
        
        // 3. 更新数量
        cart.setQuantity(commandDTO.getQuantity());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
        
        log.info("更新购物车商品数量成功，购物车ID: {}, 新数量: {}", cartId, commandDTO.getQuantity());
        return buildCartItemResponse(cart, snack);
    }

    /**
     * 删除购物车商品
     * @param cartId 购物车ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long cartId, Long userId) {
        log.info("用户{}删除购物车商品{}", userId, cartId);
        
        // 验证购物车项是否存在且属于当前用户
        Cart cart = getCartByIdAndUserId(cartId, userId);
        
        // 删除购物车项
        cartMapper.deleteById(cartId);
        
        log.info("删除购物车商品成功，购物车ID: {}", cartId);
    }

    /**
     * 批量删除购物车商品
     * @param cartIds 购物车ID列表
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteCartItems(List<Long> cartIds, Long userId) {
        log.info("用户{}批量删除购物车商品: {}", userId, cartIds);
        
        if (cartIds.isEmpty()) {
            return;
        }
        
        // 验证所有购物车项都属于当前用户
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Cart::getId, cartIds)
                   .eq(Cart::getUserId, userId);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        
        if (cartList.size() != cartIds.size()) {
            throw new BusinessException("部分购物车商品不存在或不属于当前用户");
        }
        
        // 批量删除
        cartMapper.deleteBatchIds(cartIds);
        
        log.info("批量删除购物车商品成功，删除数量: {}", cartIds.size());
    }

    /**
     * 清空用户购物车
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        log.info("清空用户{}的购物车", userId);
        
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(queryWrapper);
        
        log.info("清空购物车成功，用户ID: {}", userId);
    }

    /**
     * 获取用户购物车商品数量统计
     * @param userId 用户ID
     * @return 商品总数量
     */
    public Integer getCartItemCount(Long userId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                   .select(Cart::getQuantity);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        
        return cartList.stream()
                .mapToInt(Cart::getQuantity)
                .sum();
    }

    // ===================== 私有辅助方法 =====================

    /**
     * 校验零食是否可以加入购物车
     */
    private Snack validateSnackForCart(Long snackId, Integer quantity) {
        // 查询零食信息
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品状态
        if (!"ON_SALE".equals(snack.getStatus())) {
            throw new BusinessException("商品已下架，无法加入购物车");
        }
        
        // 检查库存
        if (snack.getStock() == null || snack.getStock() < quantity) {
            throw new BusinessException("商品库存不足，当前库存: " + (snack.getStock() == null ? 0 : snack.getStock()));
        }
        
        return snack;
    }

    /**
     * 根据ID和用户ID获取购物车项
     */
    private Cart getCartByIdAndUserId(Long cartId, Long userId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车商品不存在");
        }
        
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该购物车商品");
        }
        
        return cart;
    }

    /**
     * 构建购物车项响应DTO
     */
    private CartItemResponseDTO buildCartItemResponse(Cart cart, Snack snack) {
        // 获取分类信息
        String categoryName = null;
        if (snack.getCategoryId() != null) {
            Category category = categoryMapper.selectById(snack.getCategoryId());
            categoryName = category != null ? category.getName() : null;
        }
        
        return CartConvert.toCartItemResponse(cart, snack, categoryName);
    }
}
