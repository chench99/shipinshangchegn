package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.DTO.command.CarouselCreateDTO;
import org.example.springboot.DTO.command.CarouselUpdateDTO;
import org.example.springboot.DTO.response.CarouselResponseDTO;
import org.example.springboot.entity.Carousel;
import org.example.springboot.enumClass.FileBusinessTypeEnum;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.CarouselMapper;
import org.example.springboot.service.convert.CarouselConvert;
import org.example.springboot.DTO.FileInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    @Resource
    private FileService fileService;

    @Transactional(rollbackFor = Exception.class)
    public CarouselResponseDTO create(CarouselCreateDTO dto) {
        Carousel entity = CarouselConvert.toEntity(dto);
        validateTimeRange(entity.getStartTime(), entity.getEndTime());
        carouselMapper.insert(entity);
        return attachImage(CarouselConvert.toResponse(entity));
    }

    @Transactional(rollbackFor = Exception.class)
    public CarouselResponseDTO update(CarouselUpdateDTO dto) {
        Carousel db = carouselMapper.selectById(dto.getId());
        if (db == null) {
            throw new BusinessException("轮播图不存在");
        }
        CarouselConvert.copyToEntity(dto, db);
        validateTimeRange(db.getStartTime(), db.getEndTime());
        carouselMapper.updateById(db);
        return attachImage(CarouselConvert.toResponse(db));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        carouselMapper.deleteById(id);
        // 文件是否需要一并处理：保持不删除历史文件，按业务需要可扩展
    }

    public CarouselResponseDTO detail(Long id) {
        Carousel db = carouselMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("轮播图不存在");
        }
        return attachImage(CarouselConvert.toResponse(db));
    }

    public Page<CarouselResponseDTO> page(Long current, Long size, String status, String title) {
        Page<Carousel> page = new Page<>(current, size);
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Carousel::getTitle, title);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Carousel::getStatus, status);
        }
        wrapper.orderByAsc(Carousel::getSortOrder).orderByDesc(Carousel::getCreateTime);
        Page<Carousel> entityPage = carouselMapper.selectPage(page, wrapper);
        Page<CarouselResponseDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<CarouselResponseDTO> records = entityPage.getRecords().stream()
                .map(CarouselConvert::toResponse)
                .map(this::attachImage)
                .collect(Collectors.toList());
        dtoPage.setRecords(records);
        return dtoPage;
    }

    public List<CarouselResponseDTO> listEnabled() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carousel::getStatus, "ENABLED")
                .and(w -> w.isNull(Carousel::getStartTime).or().le(Carousel::getStartTime, now))
                .and(w -> w.isNull(Carousel::getEndTime).or().ge(Carousel::getEndTime, now))
                .orderByAsc(Carousel::getSortOrder).orderByDesc(Carousel::getCreateTime);
        List<Carousel> list = carouselMapper.selectList(wrapper);
        return list.stream().map(CarouselConvert::toResponse).map(this::attachImage).collect(Collectors.toList());
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (Objects.nonNull(start) && Objects.nonNull(end) && end.isBefore(start)) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
    }

    private CarouselResponseDTO attachImage(CarouselResponseDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        List<FileInfoDTO> files = fileService.getFilesByBusiness(FileBusinessTypeEnum.CAROUSEL_IMAGE.getCode(), String.valueOf(dto.getId()));
        if (files != null && !files.isEmpty()) {
            dto.setImageUrl(files.get(0).getFilePath());
        }
        return dto;
    }
}


