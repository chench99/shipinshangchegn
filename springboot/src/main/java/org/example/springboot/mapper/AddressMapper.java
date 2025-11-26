package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.Address;

/**
 * 收货地址Mapper接口
 * @author system
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
