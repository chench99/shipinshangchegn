package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.response.ReviewResponseDTO;
import org.example.springboot.entity.Review;
import org.example.springboot.enumClass.FileBusinessTypeEnum;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.ReviewMapper;
import org.example.springboot.mapper.OrderMapper;
import org.example.springboot.mapper.OrderItemMapper;
import org.example.springboot.entity.Order;
import org.example.springboot.entity.OrderItem;
import org.example.springboot.service.convert.ReviewConvert;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private FileService fileService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long snackId, Integer rating, String content) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未登录或Token无效");
        }

        // 校验是否购买过该零食（完成状态的订单）
        if (!canUserReview(snackId, userId)) {
            throw new BusinessException("仅购买过该商品的用户可以评价");
        }

        Review review = new Review();
        review.setSnackId(snackId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);

        return review.getId();
    }

    public Page<ReviewResponseDTO> getReviewPage(Long snackId, Long userId, Integer rating, Long current, Long size) {
        Page<Review> page = new Page<>(current, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(snackId != null, Review::getSnackId, snackId)
               .eq(userId != null, Review::getUserId, userId)
               .eq(rating != null, Review::getRating, rating)
               .orderByDesc(Review::getCreateTime);

        Page<Review> entityPage = reviewMapper.selectPage(page, wrapper);

        Page<ReviewResponseDTO> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(entityPage.getRecords().stream().map(review -> {
            ReviewResponseDTO dto = ReviewConvert.toResponseDTO(review);
            // 查询绑定的图片
            List<String> images = fileService.getFilesByBusiness(FileBusinessTypeEnum.COMMENT_IMAGE.getCode(), String.valueOf(review.getId()))
                    .stream().map(f -> f.getFilePath()).toList();
            dto.setImages(images);
            return dto;
        }).toList());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        return reviewMapper.deleteById(id) > 0;
    }

    /**
     * 判断用户是否可以评价（是否购买过该零食且订单完成）
     */
    public boolean canUserReview(Long snackId, Long userId) {
        if (snackId == null || userId == null) {
            return false;
        }
        // 查询用户已完成订单
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, userId)
                .eq(Order::getStatus, "COMPLETED");
        List<Order> orders = orderMapper.selectList(orderWrapper);
        if (orders == null || orders.isEmpty()) {
            return false;
        }
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        // 查询订单项是否包含该零食
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds)
                .eq(OrderItem::getSnackId, snackId);
        Long count = orderItemMapper.selectCount(itemWrapper);
        return count != null && count > 0;
    }
}


