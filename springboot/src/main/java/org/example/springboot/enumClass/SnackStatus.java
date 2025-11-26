package org.example.springboot.enumClass;

import lombok.Getter;

/**
 * 零食状态枚举
 * @author system
 */
@Getter
public enum SnackStatus {
    
    ON_SALE("ON_SALE", "上架"),
    OFF_SHELF("OFF_SHELF", "下架");

    private final String code;
    private final String description;

    SnackStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static SnackStatus fromCode(String code) {
        for (SnackStatus status : SnackStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的零食状态代码: " + code);
    }

    /**
     * 验证零食状态代码是否有效
     */
    public static boolean isValidCode(String code) {
        for (SnackStatus status : SnackStatus.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有有效的状态代码
     */
    public static String[] getAllCodes() {
        SnackStatus[] values = SnackStatus.values();
        String[] codes = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            codes[i] = values[i].getCode();
        }
        return codes;
    }
}

