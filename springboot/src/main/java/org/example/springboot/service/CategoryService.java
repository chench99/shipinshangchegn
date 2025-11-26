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
import java.util.stream.Collectors;

import org.example.springboot.entity.Category;
import org.example.springboot.mapper.CategoryMapper;
import org.example.springboot.DTO.command.CategoryCreateDTO;
import org.example.springboot.DTO.command.CategoryUpdateDTO;
import org.example.springboot.DTO.response.CategoryResponseDTO;
import org.example.springboot.enumClass.CategoryStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.service.convert.CategoryConvert;

/**
 * 分类业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 创建分类
     * @param createDTO 创建分类命令
     * @return 分类响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CategoryResponseDTO createCategory(CategoryCreateDTO createDTO) {
        log.info("创建分类: {}", createDTO);
        
        // 检查分类名称是否已存在
        checkCategoryNameExists(createDTO.getName(), null);
        
        // 转换并保存
        Category category = CategoryConvert.createCommandToEntity(createDTO);
        categoryMapper.insert(category);
        
        log.info("分类创建成功，ID: {}", category.getId());
        return CategoryConvert.entityToResponse(category);
    }

    /**
     * 更新分类
     * @param updateDTO 更新分类命令
     * @return 分类响应DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CategoryResponseDTO updateCategory(CategoryUpdateDTO updateDTO) {
        log.info("更新分类: {}", updateDTO);
        
        // 检查分类是否存在
        Category existingCategory = getCategoryById(updateDTO.getId());
        
        // 检查分类名称是否已被其他分类使用
        checkCategoryNameExists(updateDTO.getName(), updateDTO.getId());
        
        // 构建更新条件
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, updateDTO.getId());
        
        if (StringUtils.hasText(updateDTO.getName())) {
            updateWrapper.set(Category::getName, updateDTO.getName());
        }
        if (updateDTO.getSortOrder() != null) {
            updateWrapper.set(Category::getSortOrder, updateDTO.getSortOrder());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            // 验证状态值
            if (!CategoryStatus.isValidCode(updateDTO.getStatus())) {
                throw new BusinessException("无效的分类状态");
            }
            updateWrapper.set(Category::getStatus, updateDTO.getStatus());
        }
        
        categoryMapper.update(null, updateWrapper);
        
        // 查询更新后的数据
        Category updatedCategory = getCategoryById(updateDTO.getId());
        log.info("分类更新成功，ID: {}", updateDTO.getId());
        return CategoryConvert.entityToResponse(updatedCategory);
    }

    /**
     * 删除分类
     * @param categoryId 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        log.info("删除分类，ID: {}", categoryId);
        
        // 检查分类是否存在
        Category category = getCategoryById(categoryId);
        
        // 检查是否有关联的零食商品
        checkRelatedSnacks(categoryId);
        
        categoryMapper.deleteById(categoryId);
        log.info("分类删除成功，ID: {}", categoryId);
    }

    /**
     * 根据ID获取分类详情
     * @param categoryId 分类ID
     * @return 分类响应DTO
     */
    public CategoryResponseDTO getCategoryDetail(Long categoryId) {
        Category category = getCategoryById(categoryId);
        return CategoryConvert.entityToResponse(category);
    }

    /**
     * 分页查询分类列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 分类名称（模糊查询）
     * @param status 分类状态
     * @return 分页结果
     */
    public Page<CategoryResponseDTO> getCategoryPage(Long current, Long size, String name, String status) {
        log.info("分页查询分类列表 - 页码: {}, 大小: {}, 名称: {}, 状态: {}", current, size, name, status);
        
        Page<Category> page = new Page<>(current, size);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Category::getName, name);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Category::getStatus, status);
        }
        
        // 按排序值升序，创建时间降序
        queryWrapper.orderByAsc(Category::getSortOrder).orderByDesc(Category::getCreateTime);
        
        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<CategoryResponseDTO> responsePage = new Page<>(categoryPage.getCurrent(), categoryPage.getSize(), categoryPage.getTotal());
        List<CategoryResponseDTO> responseList = categoryPage.getRecords().stream()
                .map(CategoryConvert::entityToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responseList);
        
        return responsePage;
    }

    /**
     * 获取所有启用状态的分类列表
     * @return 分类响应DTO列表
     */
    public List<CategoryResponseDTO> getActiveCategoryList() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, CategoryStatus.ACTIVE.getCode());
        queryWrapper.orderByAsc(Category::getSortOrder).orderByDesc(Category::getCreateTime);
        
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        return categoryList.stream()
                .map(CategoryConvert::entityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新分类状态
     * @param categoryId 分类ID
     * @param status 新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryStatus(Long categoryId, String status) {
        log.info("更新分类状态，ID: {}, 状态: {}", categoryId, status);
        
        // 检查分类是否存在
        getCategoryById(categoryId);
        
        // 验证状态值
        if (!CategoryStatus.isValidCode(status)) {
            throw new BusinessException("无效的分类状态");
        }
        
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, categoryId);
        updateWrapper.set(Category::getStatus, status);
        
        categoryMapper.update(null, updateWrapper);
        log.info("分类状态更新成功，ID: {}", categoryId);
    }

    /**
     * 检查分类名称是否已存在
     * @param name 分类名称
     * @param excludeId 排除的分类ID（用于更新时排除自身）
     */
    private void checkCategoryNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(Category::getId, excludeId);
        }
        
        Long count = categoryMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }

    /**
     * 根据ID获取分类实体（内部使用）
     * @param categoryId 分类ID
     * @return 分类实体
     */
    private Category getCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    /**
     * 检查是否有关联的零食商品
     * @param categoryId 分类ID
     */
    private void checkRelatedSnacks(Long categoryId) {
        // TODO: 当零食模块开发完成后，需要检查是否有关联的零食商品
        // 目前暂时跳过此检查，因为零食表还未创建对应的实体类和服务
        log.debug("检查分类关联的零食商品，分类ID: {} (暂时跳过检查)", categoryId);
    }
}
