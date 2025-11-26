package org.example.springboot.enumClass;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 文件业务类型枚举
 * @author system
 */
@Getter
public enum FileBusinessTypeEnum {
    
    // 用户相关
    USER_AVATAR("USER_AVATAR", "用户头像", new String[]{"IMG"}),
    USER_BACKGROUND("USER_BACKGROUND", "用户背景", new String[]{"IMG"}),
    
    // 心情日记相关
    MOOD_DIARY("MOOD_DIARY", "心情日记", new String[]{"IMG", "TXT"}),
    MOOD_IMAGE("MOOD_IMAGE", "心情图片", new String[]{"IMG"}),
    
    // 帖子相关
    POST_CONTENT("POST_CONTENT", "帖子内容", new String[]{"IMG", "VIDEO"}),
    POST_COVER("POST_COVER", "帖子封面", new String[]{"IMG"}),
    
    // 评论相关
    COMMENT_IMAGE("COMMENT_IMAGE", "评论图片", new String[]{"IMG"}),
    
    // 无人机品牌相关
    DRONE_BRAND_LOGO("DRONE_BRAND_LOGO", "无人机品牌LOGO", new String[]{"IMG"}),
    
    // 无人机产品相关
    DRONE_PRODUCT_IMAGE("DRONE_PRODUCT_IMAGE", "无人机产品图片", new String[]{"IMG"}),
    DRONE_PRODUCT_MAIN("DRONE_PRODUCT_MAIN", "无人机产品主图", new String[]{"IMG"}),
    DRONE_PRODUCT_DETAIL("DRONE_PRODUCT_DETAIL", "无人机产品详情图", new String[]{"IMG"}),
    
    // 航拍作品相关
    AERIAL_WORK_FILE("AERIAL_WORK_FILE", "航拍作品文件", new String[]{"IMG", "VIDEO"}),
    AERIAL_WORK_COVER("AERIAL_WORK_COVER", "航拍作品封面", new String[]{"IMG"}),
    
    // 零食商城相关
    SNACK_COVER("SNACK_COVER", "零食封面图片", new String[]{"IMG"}),
    SNACK_DETAIL("SNACK_DETAIL", "零食详情图片", new String[]{"IMG"}),
    // 轮播图相关
    CAROUSEL_IMAGE("CAROUSEL_IMAGE", "轮播图图片", new String[]{"IMG"}),
    
    // 系统相关
    TEMP_FILE("TEMP_FILE", "临时文件", new String[]{"IMG", "PDF", "DOC", "TXT"}),
    SYSTEM_NOTICE("SYSTEM_NOTICE", "系统通知", new String[]{"IMG"});

    // Getter方法
    private final String code;
    private final String desc;

    private final String[] allowedTypes;  // 允许的文件类型


    FileBusinessTypeEnum(String code, String desc, String[] allowedTypes) {
        this.code = code;
        this.desc = desc;
        this.allowedTypes = allowedTypes;

    }

    /**
     * 根据业务类型代码获取枚举
     */
    public static FileBusinessTypeEnum getByCode(String code) {
        for (FileBusinessTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查文件类型是否允许
     */
    public static boolean isAllowedFileBussinessType(String fileBussinessType) {


        if(StrUtil.isBlank(fileBussinessType)){
            return false;
        }
        FileBusinessTypeEnum[] values = FileBusinessTypeEnum.values();
        List<String> valueCodes = Arrays.stream(values).map(FileBusinessTypeEnum::getCode).toList();
        return valueCodes.contains(fileBussinessType);

    }
    /**
     * 业务文件类型和文件类型匹配检查
     */
    public static boolean isTypeMatchBussinessType(String fileType, String bussinessType) {
        FileBusinessTypeEnum fileBusinessTypeEnum = getByCode(bussinessType);
        if(fileBusinessTypeEnum == null){
            return false;
        }

            List<String> allowTypesList = Arrays.stream(fileBusinessTypeEnum.getAllowedTypes()).toList();
            if(!allowTypesList.contains(fileType)){
                return false;
            }
        return true;


    }


} 