package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表板统计数据响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表板统计数据响应DTO")
public class DashboardStatsResponseDTO {

    @Schema(description = "用户统计")
    private UserStats userStats;

    @Schema(description = "零食商品统计")
    private SnackStats snackStats;

    @Schema(description = "订单统计")
    private OrderStats orderStats;

    @Schema(description = "分类统计")
    private CategoryStats categoryStats;

    @Schema(description = "收藏统计")
    private FavoriteStats favoriteStats;

    @Schema(description = "购物车统计")
    private CartStats cartStats;

    @Schema(description = "热门商品列表")
    private List<PopularSnackDTO> popularSnacks;

    @Schema(description = "最近订单列表")
    private List<RecentOrderDTO> recentOrders;

    @Schema(description = "订单趋势数据(最近7天)")
    private List<OrderTrendDTO> orderTrendData;

    @Schema(description = "分类销量统计数据")
    private List<CategorySalesDTO> categorySalesData;

    @Schema(description = "用户增长数据(最近12个月)")
    private List<UserGrowthDTO> userGrowthData;

    /**
     * 用户统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户统计")
    public static class UserStats {
        @Schema(description = "总用户数")
        private Long totalUsers;

        @Schema(description = "活跃用户数")
        private Long activeUsers;

        @Schema(description = "今日新增用户数")
        private Long todayNewUsers;

        @Schema(description = "本月新增用户数")
        private Long monthNewUsers;
    }

    /**
     * 零食商品统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "零食商品统计")
    public static class SnackStats {
        @Schema(description = "总商品数")
        private Long totalSnacks;

        @Schema(description = "上架商品数")
        private Long onSaleSnacks;

        @Schema(description = "下架商品数")
        private Long offShelfSnacks;

        @Schema(description = "库存不足商品数(库存<=10)")
        private Long lowStockSnacks;
    }

    /**
     * 订单统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "订单统计")
    public static class OrderStats {
        @Schema(description = "总订单数")
        private Long totalOrders;

        @Schema(description = "今日订单数")
        private Long todayOrders;

        @Schema(description = "本月订单数")
        private Long monthOrders;

        @Schema(description = "订单总金额(元)")
        private BigDecimal totalAmount;

        @Schema(description = "今日订单金额(元)")
        private BigDecimal todayAmount;

        @Schema(description = "本月订单金额(元)")
        private BigDecimal monthAmount;

        @Schema(description = "待处理订单数")
        private Long pendingOrders;

        @Schema(description = "已完成订单数")
        private Long completedOrders;
    }

    /**
     * 分类统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类统计")
    public static class CategoryStats {
        @Schema(description = "总分类数")
        private Long totalCategories;

        @Schema(description = "活跃分类数")
        private Long activeCategories;

        @Schema(description = "非活跃分类数")
        private Long inactiveCategories;
    }

    /**
     * 收藏统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "收藏统计")
    public static class FavoriteStats {
        @Schema(description = "总收藏数")
        private Long totalFavorites;

        @Schema(description = "今日收藏数")
        private Long todayFavorites;

        @Schema(description = "本月收藏数")
        private Long monthFavorites;
    }

    /**
     * 购物车统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "购物车统计")
    public static class CartStats {
        @Schema(description = "活跃购物车数(有商品的购物车)")
        private Long activeCartCount;

        @Schema(description = "购物车商品总数")
        private Long totalCartItems;
    }

    /**
     * 热门商品DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "热门商品DTO")
    public static class PopularSnackDTO {
        @Schema(description = "商品ID")
        private Long id;

        @Schema(description = "商品名称")
        private String name;

        @Schema(description = "销售数量")
        private Integer salesCount;

        @Schema(description = "价格(元)")
        private BigDecimal price;

        @Schema(description = "封面图片")
        private String coverImage;
    }

    /**
     * 最近订单DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "最近订单DTO")
    public static class RecentOrderDTO {
        @Schema(description = "订单ID")
        private Long id;

        @Schema(description = "订单号")
        private String orderNo;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "订单金额(元)")
        private BigDecimal totalAmount;

        @Schema(description = "订单状态")
        private String status;

        @Schema(description = "创建时间")
        private String createTime;
    }

    /**
     * 订单趋势DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "订单趋势DTO")
    public static class OrderTrendDTO {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "订单数量")
        private Long orderCount;

        @Schema(description = "销售额(元)")
        private BigDecimal revenue;
    }

    /**
     * 分类销量DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类销量DTO")
    public static class CategorySalesDTO {
        @Schema(description = "分类名称")
        private String categoryName;

        @Schema(description = "销量")
        private Integer salesCount;

        @Schema(description = "商品数量")
        private Integer snackCount;
    }

    /**
     * 用户增长DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户增长DTO")
    public static class UserGrowthDTO {
        @Schema(description = "月份")
        private String month;

        @Schema(description = "新增用户数")
        private Long newUsers;

        @Schema(description = "累计用户数")
        private Long totalUsers;
    }
}
