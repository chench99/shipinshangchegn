package org.example.springboot.service.convert;

import org.example.springboot.DTO.response.FavoriteStatusResponseDTO;
import org.example.springboot.entity.Favorite;

import java.time.LocalDateTime;

/**
 * 收藏数据转换工具类
 * @author system
 */
public class FavoriteConvert {

    /**
     * 创建收藏实体
     * @param userId 用户ID
     * @param snackId 商品ID
     * @return 收藏实体
     */
    public static Favorite createFavoriteEntity(Long userId, Long snackId) {
        return Favorite.builder()
                .userId(userId)
                .snackId(snackId)
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 构建收藏状态响应DTO
     * @param snackId 商品ID
     * @param isFavorited 是否已收藏
     * @param favoriteCount 收藏总数
     * @return 收藏状态响应DTO
     */
    public static FavoriteStatusResponseDTO buildStatusResponse(Long snackId, Boolean isFavorited, Integer favoriteCount) {
        return FavoriteStatusResponseDTO.builder()
                .snackId(snackId)
                .isFavorited(isFavorited)
                .favoriteCount(favoriteCount)
                .build();
    }
}
