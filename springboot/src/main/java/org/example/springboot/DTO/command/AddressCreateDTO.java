package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 收货地址创建DTO
 * @author system
 */
@Data
@Schema(description = "收货地址创建DTO")
public class AddressCreateDTO {

    @Schema(description = "收货人姓名")
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 100, message = "收货人姓名长度不能超过100个字符")
    private String consigneeName;

    @Schema(description = "收货人手机号")
    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    @Schema(description = "省份")
    @Size(max = 100, message = "省份长度不能超过100个字符")
    private String province;

    @Schema(description = "城市")
    @Size(max = 100, message = "城市长度不能超过100个字符")
    private String city;

    @Schema(description = "区/县")
    @Size(max = 100, message = "区/县长度不能超过100个字符")
    private String district;

    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址长度不能超过255个字符")
    private String detailedAddress;

    @Schema(description = "是否设为默认地址")
    private Boolean isDefault;
}
