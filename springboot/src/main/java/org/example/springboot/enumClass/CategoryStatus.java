package org.example.springboot.enumClass;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分类状态枚举
 * @author system
 */
@Getter
@AllArgsConstructor
public enum CategoryStatus {
    
    /**
     * 启用状态
     */
    ACTIVE("ACTIVE", "启用"),
    
    /**
     * 禁用状态
     */
    INACTIVE("INACTIVE", "禁用");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static CategoryStatus fromCode(String code) {
        for (CategoryStatus status : CategoryStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断代码是否有效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}
