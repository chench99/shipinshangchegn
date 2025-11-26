package org.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.response.DashboardStatsResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.mapper.*;
import org.example.springboot.util.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表板统计服务
 * @author system
 */
@Service
@Slf4j
public class DashboardStatsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnackMapper snackMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private CartMapper cartMapper;

    /**
     * 获取仪表板统计数据
     */
    public DashboardStatsResponseDTO getDashboardStats() {
        log.info("开始获取仪表板统计数据");

        return DashboardStatsResponseDTO.builder()
                .userStats(getUserStats())
                .snackStats(getSnackStats())
                .orderStats(getOrderStats())
                .categoryStats(getCategoryStats())
                .favoriteStats(getFavoriteStats())
                .cartStats(getCartStats())
                .popularSnacks(getPopularSnacks())
                .recentOrders(getRecentOrders())
                .orderTrendData(getOrderTrendData())
                .categorySalesData(getCategorySalesData())
                .userGrowthData(getUserGrowthData())
                .build();
    }

    /**
     * 获取用户统计数据
     */
    private DashboardStatsResponseDTO.UserStats getUserStats() {
        // 总用户数
        Long totalUsers = userMapper.selectCount(null);

        // 活跃用户数（状态为ACTIVE）
        Long activeUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, "ACTIVE")
        );

        // 今日新增用户数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        Long todayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, todayStart, todayEnd)
        );

        // 本月新增用户数
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Long monthNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, monthStart, LocalDateTime.now())
        );

        return DashboardStatsResponseDTO.UserStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .todayNewUsers(todayNewUsers)
                .monthNewUsers(monthNewUsers)
                .build();
    }

    /**
     * 获取零食商品统计数据
     */
    private DashboardStatsResponseDTO.SnackStats getSnackStats() {
        // 总商品数
        Long totalSnacks = snackMapper.selectCount(null);

        // 上架商品数
        Long onSaleSnacks = snackMapper.selectCount(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getStatus, "ON_SALE")
        );

        // 下架商品数
        Long offShelfSnacks = snackMapper.selectCount(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getStatus, "OFF_SHELF")
        );

        // 库存不足商品数（库存<=10）
        Long lowStockSnacks = snackMapper.selectCount(
                new LambdaQueryWrapper<Snack>()
                        .le(Snack::getStock, 10)
        );

        return DashboardStatsResponseDTO.SnackStats.builder()
                .totalSnacks(totalSnacks)
                .onSaleSnacks(onSaleSnacks)
                .offShelfSnacks(offShelfSnacks)
                .lowStockSnacks(lowStockSnacks)
                .build();
    }

    /**
     * 获取订单统计数据
     */
    private DashboardStatsResponseDTO.OrderStats getOrderStats() {
        // 总订单数
        Long totalOrders = orderMapper.selectCount(null);

        // 今日订单数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        Long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .between(Order::getCreateTime, todayStart, todayEnd)
        );

        // 本月订单数
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Long monthOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .between(Order::getCreateTime, monthStart, LocalDateTime.now())
        );

        // 计算订单金额统计
        List<Order> allOrders = orderMapper.selectList(null);
        List<Order> todayOrderList = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .between(Order::getCreateTime, todayStart, todayEnd)
        );
        List<Order> monthOrderList = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .between(Order::getCreateTime, monthStart, LocalDateTime.now())
        );

        // 订单总金额(分转元)
        BigDecimal totalAmount = allOrders.stream()
                .map(order -> new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 今日订单金额
        BigDecimal todayAmount = todayOrderList.stream()
                .map(order -> new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 本月订单金额
        BigDecimal monthAmount = monthOrderList.stream()
                .map(order -> new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 待处理订单数
        Long pendingOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, "PENDING")
        );

        // 已完成订单数
        Long completedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, "COMPLETED")
        );

        return DashboardStatsResponseDTO.OrderStats.builder()
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .monthOrders(monthOrders)
                .totalAmount(totalAmount)
                .todayAmount(todayAmount)
                .monthAmount(monthAmount)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .build();
    }

    /**
     * 获取分类统计数据
     */
    private DashboardStatsResponseDTO.CategoryStats getCategoryStats() {
        // 总分类数
        Long totalCategories = categoryMapper.selectCount(null);

        // 活跃分类数
        Long activeCategories = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, "ACTIVE")
        );

        // 非活跃分类数
        Long inactiveCategories = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, "INACTIVE")
        );

        return DashboardStatsResponseDTO.CategoryStats.builder()
                .totalCategories(totalCategories)
                .activeCategories(activeCategories)
                .inactiveCategories(inactiveCategories)
                .build();
    }

    /**
     * 获取收藏统计数据
     */
    private DashboardStatsResponseDTO.FavoriteStats getFavoriteStats() {
        // 总收藏数
        Long totalFavorites = favoriteMapper.selectCount(null);

        // 今日收藏数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        Long todayFavorites = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .between(Favorite::getCreateTime, todayStart, todayEnd)
        );

        // 本月收藏数
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Long monthFavorites = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .between(Favorite::getCreateTime, monthStart, LocalDateTime.now())
        );

        return DashboardStatsResponseDTO.FavoriteStats.builder()
                .totalFavorites(totalFavorites)
                .todayFavorites(todayFavorites)
                .monthFavorites(monthFavorites)
                .build();
    }

    /**
     * 获取购物车统计数据
     */
    private DashboardStatsResponseDTO.CartStats getCartStats() {
        // 活跃购物车数（有商品的购物车，按用户ID去重）
        Long activeCartCount = cartMapper.selectCount(
                new LambdaQueryWrapper<Cart>()
                        .gt(Cart::getQuantity, 0)
                        .groupBy(Cart::getUserId)
        );

        // 购物车商品总数
        List<Cart> cartItems = cartMapper.selectList(null);
        Long totalCartItems = cartItems.stream()
                .mapToLong(Cart::getQuantity)
                .sum();

        return DashboardStatsResponseDTO.CartStats.builder()
                .activeCartCount(activeCartCount)
                .totalCartItems(totalCartItems)
                .build();
    }

    /**
     * 获取热门商品列表（按销售量排序，取前5个）
     */
    private List<DashboardStatsResponseDTO.PopularSnackDTO> getPopularSnacks() {
        List<Snack> popularSnacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getStatus, "ON_SALE")
                        .orderByDesc(Snack::getSalesCount)
                        .last("LIMIT 5")
        );

        return popularSnacks.stream()
                .map(snack -> DashboardStatsResponseDTO.PopularSnackDTO.builder()
                        .id(snack.getId())
                        .name(snack.getName())
                        .salesCount(snack.getSalesCount())
                        .price(new BigDecimal(snack.getPrice()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                        .coverImage(snack.getCoverImage())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取最近订单列表（取最近10个订单）
     */
    private List<DashboardStatsResponseDTO.RecentOrderDTO> getRecentOrders() {
        List<Order> recentOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .orderByDesc(Order::getCreateTime)
                        .last("LIMIT 10")
        );

        return recentOrders.stream()
                .map(order -> {
                    // 获取用户信息
                    User user = userMapper.selectById(order.getUserId());
                    String username = user != null ? user.getUsername() : "未知用户";

                    return DashboardStatsResponseDTO.RecentOrderDTO.builder()
                            .id(order.getId())
                            .orderNo(order.getOrderNo())
                            .username(username)
                            .totalAmount(new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                            .status(order.getStatus())
                            .createTime(DateUtils.formatDateTime(order.getCreateTime()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取订单趋势数据（最近7天）
     */
    private List<DashboardStatsResponseDTO.OrderTrendDTO> getOrderTrendData() {
        List<DashboardStatsResponseDTO.OrderTrendDTO> trendData = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            
            // 获取当天订单数量
            Long orderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Order>()
                            .between(Order::getCreateTime, dayStart, dayEnd)
            );
            
            // 获取当天订单金额
            List<Order> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .between(Order::getCreateTime, dayStart, dayEnd)
            );
            
            BigDecimal dayRevenue = dayOrders.stream()
                    .map(order -> new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            trendData.add(DashboardStatsResponseDTO.OrderTrendDTO.builder()
                    .date(DateUtils.formatDate(date))
                    .orderCount(orderCount)
                    .revenue(dayRevenue)
                    .build());
        }
        
        return trendData;
    }

    /**
     * 获取分类销量统计数据
     */
    private List<DashboardStatsResponseDTO.CategorySalesDTO> getCategorySalesData() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, "ACTIVE")
        );
        
        return categories.stream()
                .map(category -> {
                    // 获取该分类下的商品
                    List<Snack> snacks = snackMapper.selectList(
                            new LambdaQueryWrapper<Snack>()
                                    .eq(Snack::getCategoryId, category.getId())
                                    .eq(Snack::getStatus, "ON_SALE")
                    );
                    
                    // 计算该分类的总销量
                    Integer totalSales = snacks.stream()
                            .mapToInt(snack -> snack.getSalesCount() != null ? snack.getSalesCount() : 0)
                            .sum();
                    
                    return DashboardStatsResponseDTO.CategorySalesDTO.builder()
                            .categoryName(category.getName())
                            .salesCount(totalSales)
                            .snackCount(snacks.size())
                            .build();
                })
                .filter(dto -> dto.getSalesCount() > 0) // 只返回有销量的分类
                .sorted((a, b) -> b.getSalesCount().compareTo(a.getSalesCount())) // 按销量降序
                .collect(Collectors.toList());
    }

    /**
     * 获取用户增长数据（最近12个月）
     */
    private List<DashboardStatsResponseDTO.UserGrowthDTO> getUserGrowthData() {
        List<DashboardStatsResponseDTO.UserGrowthDTO> growthData = new ArrayList<>();
        
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
            LocalDateTime monthStartTime = monthStart.atStartOfDay();
            LocalDateTime monthEndTime = monthEnd.atTime(LocalTime.MAX);
            
            // 获取该月新增用户数
            Long newUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .between(User::getCreateTime, monthStartTime, monthEndTime)
            );
            
            // 获取该月结束时的累计用户数
            Long totalUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .le(User::getCreateTime, monthEndTime)
            );
            
            String monthStr = monthStart.getYear() + "-" + 
                            String.format("%02d", monthStart.getMonthValue());
            
            growthData.add(DashboardStatsResponseDTO.UserGrowthDTO.builder()
                    .month(monthStr)
                    .newUsers(newUsers)
                    .totalUsers(totalUsers)
                    .build());
        }
        
        return growthData;
    }
}
