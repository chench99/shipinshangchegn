package org.example.springboot.service.convert;

import org.example.springboot.DTO.command.UserRegisterCommandDTO;
import org.example.springboot.DTO.response.UserDetailResponseDTO;
import org.example.springboot.DTO.response.UserLoginResponseDTO;

import org.example.springboot.entity.User;
import org.example.springboot.enumClass.UserStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户转换类
 * @author system
 */
public class UserConvert {

    /**
     * 注册命令DTO转换为User实体
     * @param registerDTO 注册命令DTO
     * @param encodedPassword 加密后的密码
     * @return User实体
     */
    public static User registerCommandToEntity(UserRegisterCommandDTO registerDTO, String encodedPassword) {
        return User.builder()
                .username(registerDTO.getUsername())
                .password(encodedPassword)
                .nickname(StringUtils.hasText(registerDTO.getNickname()) ? registerDTO.getNickname() : null)
                .phone(StringUtils.hasText(registerDTO.getPhone()) ? registerDTO.getPhone() : null)
                .userType(registerDTO.getUserType())
                .status(UserStatus.ACTIVE.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * User实体转换为详情响应DTO
     * @param user User实体
     * @return 用户详情响应DTO
     */
    public static UserDetailResponseDTO entityToDetailResponse(User user) {
        return UserDetailResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    /**
     * 构建登录响应DTO
     * @param token JWT令牌
     * @param userInfo 用户信息
     * @return 登录响应DTO
     */
    public static UserLoginResponseDTO buildLoginResponse(String token, UserDetailResponseDTO userInfo) {
        return UserLoginResponseDTO.builder()
                .userInfo(userInfo)
                .token(token)
                .roleType(userInfo.getUserType())
                .build();
    }


}
