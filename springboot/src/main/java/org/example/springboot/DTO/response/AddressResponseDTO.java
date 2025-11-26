package org.example.springboot.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址响应DTO
 * @author system
 */
@Data
@Schema(description = "收货地址响应DTO")
public class AddressResponseDTO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货人姓名")
    private String consigneeName;

    @Schema(description = "收货人手机号")
    private String phone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detailedAddress;

    @Schema(description = "完整地址")
    private String fullAddress;

    @Schema(description = "是否为默认地址")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
