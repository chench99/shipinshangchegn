package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_address")
@Schema(description = "收货地址实体类")
public class Address {

    @TableId(type = IdType.AUTO)
    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "收货人姓名")
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 100, message = "收货人姓名长度不能超过100个字符")
    @TableField("consignee_name")
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
    @TableField("detailed_address")
    private String detailedAddress;

    @Schema(description = "是否为默认地址")
    @TableField("is_default")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
