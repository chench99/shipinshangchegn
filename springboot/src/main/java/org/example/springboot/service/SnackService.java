package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.springboot.entity.Snack;
import org.example.springboot.entity.Category;
import org.example.springboot.mapper.SnackMapper;
import org.example.springboot.mapper.CategoryMapper;
import org.example.springboot.DTO.command.SnackCreateDTO;
import org.example.springboot.DTO.command.SnackUpdateDTO;
import org.example.springboot.DTO.response.SnackResponseDTO;
import org.example.springboot.DTO.response.SnackListResponseDTO;
import org.example.springboot.enumClass.SnackStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.service.convert.SnackConvert;

/**
 * 零食业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class SnackService {

    @Resource
    private SnackMapper snackMapper;

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 创建零食
     * @param createDTO 创建零食命令
     * @return 零食响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SnackResponseDTO createSnack(SnackCreateDTO createDTO) {
        log.info("创建零食: {}", createDTO);
        
        // 检查分类是否存在
        Category category = getCategoryById(createDTO.getCategoryId());
        
        // 检查零食名称是否已存在
        checkSnackNameExists(createDTO.getName(), null);
        
        // 转换并保存
        Snack snack = SnackConvert.createCommandToEntity(createDTO);
        snackMapper.insert(snack);
        
        log.info("零食创建成功，ID: {}", snack.getId());
        return SnackConvert.entityToResponse(snack, category.getName());
    }

    /**
     * 更新零食
     * @param updateDTO 更新零食命令
     * @return 零食响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SnackResponseDTO updateSnack(SnackUpdateDTO updateDTO) {
        log.info("更新零食: {}", updateDTO);
        
        // 检查零食是否存在
        Snack existingSnack = getSnackById(updateDTO.getId());
        
        // 检查分类是否存在
        Category category = getCategoryById(updateDTO.getCategoryId());
        
        // 检查零食名称是否已被其他零食使用
        checkSnackNameExists(updateDTO.getName(), updateDTO.getId());
        
        // 构建更新条件
        LambdaUpdateWrapper<Snack> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Snack::getId, updateDTO.getId());
        
        updateWrapper.set(Snack::getCategoryId, updateDTO.getCategoryId());
        updateWrapper.set(Snack::getName, updateDTO.getName());
        updateWrapper.set(Snack::getDescription, updateDTO.getDescription());
        updateWrapper.set(Snack::getPrice, updateDTO.getPrice().multiply(java.math.BigDecimal.valueOf(100)).intValue());
        updateWrapper.set(Snack::getStock, updateDTO.getStock());
        updateWrapper.set(Snack::getCoverImage, updateDTO.getCoverImage());
        updateWrapper.set(Snack::getDetailImages, SnackConvert.listToJsonString(updateDTO.getDetailImages()));
        
        if (StringUtils.hasText(updateDTO.getStatus())) {
            // 验证状态值
            if (!SnackStatus.isValidCode(updateDTO.getStatus())) {
                throw new BusinessException("无效的零食状态");
            }
            updateWrapper.set(Snack::getStatus, updateDTO.getStatus());
        }
        
        snackMapper.update(null, updateWrapper);
        
        // 查询更新后的数据
        Snack updatedSnack = getSnackById(updateDTO.getId());
        log.info("零食更新成功，ID: {}", updateDTO.getId());
        return SnackConvert.entityToResponse(updatedSnack, category.getName());
    }

    /**
     * 删除零食
     * @param snackId 零食ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSnack(Long snackId) {
        log.info("删除零食，ID: {}", snackId);
        
        // 检查零食是否存在
        getSnackById(snackId);
        
        // 检查关联数据（如订单项等）
        checkRelatedData(snackId);
        
        snackMapper.deleteById(snackId);
        log.info("零食删除成功，ID: {}", snackId);
    }

    /**
     * 根据ID获取零食详情
     * @param snackId 零食ID
     * @return 零食响应DTO
     */
    public SnackResponseDTO getSnackDetail(Long snackId) {
        log.info("获取零食详情，ID: {}", snackId);
        
        Snack snack = getSnackById(snackId);
        Category category = getCategoryById(snack.getCategoryId());
        
        return SnackConvert.entityToResponse(snack, category.getName());
    }

    /**
     * 分页查询零食列表
     * @param current 当前页
     * @param size 每页大小
     * @param name 零食名称（模糊查询）
     * @param categoryId 分类ID
     * @param status 状态
     * @param minPrice 最低价格（元）
     * @param maxPrice 最高价格（元）
     * @return 分页结果
     */
    public Page<SnackListResponseDTO> getSnackPage(Long current, Long size, String name, Long categoryId, 
                                                   String status, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        log.info("分页查询零食列表，当前页: {}, 每页大小: {}, 名称: {}, 分类ID: {}, 状态: {}, 价格范围: {}-{}", 
                current, size, name, categoryId, status, minPrice, maxPrice);
        
        Page<Snack> page = new Page<>(current, size);
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (StringUtils.hasText(name)) {
            wrapper.like(Snack::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Snack::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Snack::getStatus, status);
        }
        if (minPrice != null) {
            wrapper.ge(Snack::getPrice, minPrice.multiply(java.math.BigDecimal.valueOf(100)).intValue());
        }
        if (maxPrice != null) {
            wrapper.le(Snack::getPrice, maxPrice.multiply(java.math.BigDecimal.valueOf(100)).intValue());
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(Snack::getCreateTime);
        
        Page<Snack> snackPage = snackMapper.selectPage(page, wrapper);
        
        // 获取所有分类信息用于转换
        Map<Long, String> categoryMap = getCategoryMap(snackPage.getRecords());
        
        // 转换为响应DTO
        Page<SnackListResponseDTO> result = new Page<>();
        result.setCurrent(snackPage.getCurrent());
        result.setSize(snackPage.getSize());
        result.setTotal(snackPage.getTotal());
        result.setRecords(snackPage.getRecords().stream()
                .map(snack -> SnackConvert.entityToListResponse(snack, categoryMap.get(snack.getCategoryId())))
                .collect(Collectors.toList()));
        
        return result;
    }

    /**
     * 获取前台展示的零食列表（只显示上架商品）
     * @param current 当前页
     * @param size 每页大小
     * @param name 零食名称（模糊查询）
     * @param categoryId 分类ID
     * @param sortBy 排序字段（price, sales_count, create_time）
     * @param sortOrder 排序方向（asc, desc）
     * @param minPrice 最低价格（元）
     * @param maxPrice 最高价格（元）
     * @return 分页结果
     */
    public Page<SnackListResponseDTO> getFrontendSnackPage(Long current, Long size, String name, Long categoryId, 
                                                           String sortBy, String sortOrder, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        log.info("前台分页查询零食列表，当前页: {}, 每页大小: {}, 名称: {}, 分类ID: {}, 排序: {} {}, 价格范围: {}-{}", 
                current, size, name, categoryId, sortBy, sortOrder, minPrice, maxPrice);
        
        Page<Snack> page = new Page<>(current, size);
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        
        // 只显示上架商品
        wrapper.eq(Snack::getStatus, SnackStatus.ON_SALE.getCode());
        
        // 构建查询条件
        if (StringUtils.hasText(name)) {
            wrapper.like(Snack::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Snack::getCategoryId, categoryId);
        }
        // 价格筛选（数据库中以分为单位存储，需要转换）
        if (minPrice != null) {
            wrapper.ge(Snack::getPrice, minPrice.multiply(java.math.BigDecimal.valueOf(100)).intValue());
        }
        if (maxPrice != null) {
            wrapper.le(Snack::getPrice, maxPrice.multiply(java.math.BigDecimal.valueOf(100)).intValue());
        }
        
        // 排序逻辑
        if (StringUtils.hasText(sortBy)) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
            switch (sortBy.toLowerCase()) {
                case "price":
                    if (isAsc) {
                        wrapper.orderByAsc(Snack::getPrice);
                    } else {
                        wrapper.orderByDesc(Snack::getPrice);
                    }
                    break;
                case "sales_count":
                    if (isAsc) {
                        wrapper.orderByAsc(Snack::getSalesCount);
                    } else {
                        wrapper.orderByDesc(Snack::getSalesCount);
                    }
                    break;
                case "create_time":
                    if (isAsc) {
                        wrapper.orderByAsc(Snack::getCreateTime);
                    } else {
                        wrapper.orderByDesc(Snack::getCreateTime);
                    }
                    break;
                default:
                    wrapper.orderByDesc(Snack::getCreateTime);
                    break;
            }
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(Snack::getCreateTime);
        }
        
        Page<Snack> snackPage = snackMapper.selectPage(page, wrapper);
        
        // 获取所有分类信息用于转换
        Map<Long, String> categoryMap = getCategoryMap(snackPage.getRecords());
        
        // 转换为响应DTO
        Page<SnackListResponseDTO> result = new Page<>();
        result.setCurrent(snackPage.getCurrent());
        result.setSize(snackPage.getSize());
        result.setTotal(snackPage.getTotal());
        result.setRecords(snackPage.getRecords().stream()
                .map(snack -> SnackConvert.entityToListResponse(snack, categoryMap.get(snack.getCategoryId())))
                .collect(Collectors.toList()));
        
        return result;
    }

    /**
     * 获取推荐零食列表（首页展示）
     * @param limit 限制数量
     * @return 零食列表
     */
    public List<SnackListResponseDTO> getRecommendedSnacks(int limit) {
        log.info("获取推荐零食列表，限制数量: {}", limit);
        
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Snack::getStatus, SnackStatus.ON_SALE.getCode());
        wrapper.orderByDesc(Snack::getSalesCount, Snack::getCreateTime);
        wrapper.last("LIMIT " + limit);
        
        List<Snack> snacks = snackMapper.selectList(wrapper);
        
        // 获取所有分类信息用于转换
        Map<Long, String> categoryMap = getCategoryMap(snacks);
        
        return snacks.stream()
                .map(snack -> SnackConvert.entityToListResponse(snack, categoryMap.get(snack.getCategoryId())))
                .collect(Collectors.toList());
    }

    /**
     * 更新零食状态
     * @param snackId 零食ID
     * @param status 新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSnackStatus(Long snackId, String status) {
        log.info("更新零食状态，ID: {}, 状态: {}", snackId, status);
        
        // 检查零食是否存在
        getSnackById(snackId);
        
        // 验证状态值
        if (!SnackStatus.isValidCode(status)) {
            throw new BusinessException("无效的零食状态");
        }
        
        LambdaUpdateWrapper<Snack> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Snack::getId, snackId);
        updateWrapper.set(Snack::getStatus, status);
        
        snackMapper.update(null, updateWrapper);
        log.info("零食状态更新成功，ID: {}, 新状态: {}", snackId, status);
    }

    /**
     * 调整零食库存
     * @param  id 零食ID
     * @param  quantity 调整数量（正数增加，负数减少）
     */
    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(Long id, Integer quantity) {
        log.info("调整库存: snackId={}, quantity={}" , id, quantity);
        
        Snack snack = getSnackById(id); // 使用现有的 getSnackById 方法
        int newStock = snack.getStock() + quantity;
        
        if (newStock < 0 ) {
            throw new BusinessException("库存不足，操作失败" );
        }
        
        snack.setStock(newStock);
        snackMapper.updateById(snack);
        log.info("库存调整成功，当前库存: {}" , newStock);
    }

    /**
     * 根据ID获取零食实体（内部方法）
     * @param snackId 零食ID
     * @return 零食实体
     */
    private Snack getSnackById(Long snackId) {
        if (snackId == null) {
            throw new BusinessException("零食ID不能为空");
        }
        
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null) {
            throw new BusinessException("零食不存在");
        }
        
        return snack;
    }

    /**
     * 根据ID获取分类实体（内部方法）
     * @param categoryId 分类ID
     * @return 分类实体
     */
    private Category getCategoryById(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }
        
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        return category;
    }

    /**
     * 检查零食名称是否已存在
     * @param name 零食名称
     * @param excludeId 排除的零食ID（用于更新时排除自己）
     */
    private void checkSnackNameExists(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return;
        }
        
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Snack::getName, name);
        if (excludeId != null) {
            wrapper.ne(Snack::getId, excludeId);
        }
        
        Long count = snackMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("零食名称已存在");
        }
    }

    /**
     * 检查关联数据
     * @param snackId 零食ID
     */
    private void checkRelatedData(Long snackId) {
        // TODO: 检查订单项等关联数据
        // 当订单模块开发完成后，需要在这里检查是否有关联的订单项
        log.debug("检查零食关联数据，ID: {}", snackId);
    }

    /**
     * 获取分类映射
     * @param snacks 零食列表
     * @return 分类ID到分类名称的映射
     */
    private Map<Long, String> getCategoryMap(List<Snack> snacks) {
        List<Long> categoryIds = snacks.stream()
                .map(Snack::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        
        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }
}

