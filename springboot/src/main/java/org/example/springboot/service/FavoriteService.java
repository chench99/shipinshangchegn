package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.response.FavoriteResponseDTO;
import org.example.springboot.DTO.response.FavoriteStatusResponseDTO;
import org.example.springboot.entity.Favorite;
import org.example.springboot.entity.Snack;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.FavoriteMapper;
import org.example.springboot.mapper.SnackMapper;
import org.example.springboot.service.convert.FavoriteConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 收藏业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private SnackMapper snackMapper;

    /**
     * 切换收藏状态（收藏/取消收藏）
     * @param snackId 商品ID
     * @param userId 用户ID
     * @return 收藏状态响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public FavoriteStatusResponseDTO toggleFavorite(Long snackId, Long userId) {
        log.info("用户{}切换商品{}的收藏状态", userId, snackId);
        
        // 1. 校验商品是否存在且可收藏
        Snack snack = validateSnackForFavorite(snackId);
        
        // 2. 检查当前收藏状态
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getSnackId, snackId);
        Favorite existingFavorite = favoriteMapper.selectOne(queryWrapper);
        
        boolean isFavorited;
        if (existingFavorite != null) {
            // 已收藏，执行取消收藏
            favoriteMapper.deleteById(existingFavorite.getId());
            isFavorited = false;
            log.info("用户{}取消收藏商品{}成功", userId, snackId);
        } else {
            // 未收藏，执行收藏
            Favorite newFavorite = FavoriteConvert.createFavoriteEntity(userId, snackId);
            favoriteMapper.insert(newFavorite);
            isFavorited = true;
            log.info("用户{}收藏商品{}成功", userId, snackId);
        }
        
        // 3. 获取最新的收藏总数
        Integer favoriteCount = favoriteMapper.countBySnackId(snackId);
        
        return FavoriteConvert.buildStatusResponse(snackId, isFavorited, favoriteCount);
    }

    /**
     * 检查商品收藏状态
     * @param snackId 商品ID
     * @param userId 用户ID
     * @return 收藏状态响应DTO
     */
    public FavoriteStatusResponseDTO checkFavoriteStatus(Long snackId, Long userId) {
        log.info("检查用户{}对商品{}的收藏状态", userId, snackId);
        
        // 1. 校验商品是否存在
        validateSnackForFavorite(snackId);
        
        // 2. 检查收藏状态
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getSnackId, snackId);
        boolean isFavorited = favoriteMapper.selectCount(queryWrapper) > 0;
        
        // 3. 获取收藏总数
        Integer favoriteCount = favoriteMapper.countBySnackId(snackId);
        
        return FavoriteConvert.buildStatusResponse(snackId, isFavorited, favoriteCount);
    }

    /**
     * 获取用户收藏列表（分页）
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 收藏列表分页结果
     */
    public Page<FavoriteResponseDTO> getUserFavoriteList(Long userId, Long current, Long size) {
        log.info("获取用户{}的收藏列表，页码: {}, 每页大小: {}", userId, current, size);
        
        Page<FavoriteResponseDTO> page = new Page<>(current, size);
        return favoriteMapper.selectUserFavoritesWithDetails(page, userId);
    }

    /**
     * 获取用户收藏总数
     * @param userId 用户ID
     * @return 收藏总数
     */
    public Integer getUserFavoriteCount(Long userId) {
        log.info("获取用户{}的收藏总数", userId);
        
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId);
        return Math.toIntExact(favoriteMapper.selectCount(queryWrapper));
    }

    /**
     * 批量检查商品收藏状态
     * @param snackIds 商品ID列表
     * @param userId 用户ID
     * @return 商品ID和收藏状态的映射
     */
    public java.util.Map<Long, Boolean> batchCheckFavoriteStatus(java.util.List<Long> snackIds, Long userId) {
        log.info("批量检查用户{}对商品{}的收藏状态", userId, snackIds);
        
        if (snackIds == null || snackIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        
        // 查询用户对这些商品的收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .in(Favorite::getSnackId, snackIds);
        java.util.List<Favorite> favorites = favoriteMapper.selectList(queryWrapper);
        
        // 构建收藏状态映射
        java.util.Set<Long> favoritedSnackIds = favorites.stream()
                .map(Favorite::getSnackId)
                .collect(java.util.stream.Collectors.toSet());
        
        return snackIds.stream()
                .collect(java.util.stream.Collectors.toMap(
                        snackId -> snackId,
                        favoritedSnackIds::contains
                ));
    }

    /**
     * 校验商品是否可收藏
     * @param snackId 商品ID
     * @return 商品实体
     */
    private Snack validateSnackForFavorite(Long snackId) {
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null) {
            throw new BusinessException("商品不存在，商品ID: " + snackId);
        }
        
        if (!"ON_SALE".equals(snack.getStatus())) {
            throw new BusinessException("商品已下架，无法收藏");
        }
        
        return snack;
    }
}
