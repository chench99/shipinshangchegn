package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.OrderItem;

/**
 * 订单项Mapper接口
 * @author system
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
