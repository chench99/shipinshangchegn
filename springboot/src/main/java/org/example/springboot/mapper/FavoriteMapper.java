package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.DTO.response.FavoriteResponseDTO;
import org.example.springboot.entity.Favorite;

/**
 * 收藏数据访问层
 * @author system
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 分页查询用户收藏列表（包含商品详细信息）
     * @param page 分页对象
     * @param userId 用户ID
     * @return 收藏列表
     */
    @Select("SELECT " +
            "f.id AS favoriteId, " +
            "f.create_time AS favoriteTime, " +
            "s.id AS snackId, " +
            "s.name AS snackName, " +
            "s.description AS snackDescription, " +
            "ROUND(s.price / 100.0, 2) AS price, " +
            "s.stock, " +
            "s.cover_image AS coverImage, " +
            "s.status, " +
            "s.sales_count AS salesCount, " +
            "s.favorite_count AS favoriteCount, " +
            "c.id AS categoryId, " +
            "c.name AS categoryName, " +
            "CASE WHEN s.status = 'ON_SALE' AND s.stock > 0 THEN 1 ELSE 0 END AS canPurchase " +
            "FROM t_favorite f " +
            "LEFT JOIN t_snack s ON f.snack_id = s.id " +
            "LEFT JOIN t_category c ON s.category_id = c.id " +
            "WHERE f.user_id = #{userId} AND s.status = 'ON_SALE' " +
            "ORDER BY f.create_time DESC")
    Page<FavoriteResponseDTO> selectUserFavoritesWithDetails(Page<FavoriteResponseDTO> page, @Param("userId") Long userId);

    /**
     * 获取商品的收藏总数
     * @param snackId 商品ID
     * @return 收藏总数
     */
    @Select("SELECT COUNT(*) FROM t_favorite WHERE snack_id = #{snackId}")
    Integer countBySnackId(@Param("snackId") Long snackId);
}
