/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : snack_db_2025

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 17/09/2025 15:59:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_file_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_info`;
CREATE TABLE `sys_file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件访问路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型(IMG/PDF/TXT/AUDIO/VIDEO)',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `business_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务对象ID',
  `business_field` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务字段名',
  `upload_user_id` bigint NULL DEFAULT NULL COMMENT '上传用户ID',
  `is_temp` tinyint(1) NULL DEFAULT 0 COMMENT '是否临时文件(0:否 1:是)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:删除 1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间(临时文件)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_business_field`(`business_type` ASC, `business_id` ASC, `business_field` ASC) USING BTREE,
  INDEX `idx_upload_user`(`upload_user_id` ASC) USING BTREE,
  INDEX `idx_is_temp`(`is_temp` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_file_path`(`file_path` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表-精简版' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file_info
-- ----------------------------
INSERT INTO `sys_file_info` VALUES (18, 'image323232s.jpg', '/files/temp/1757916488895.jpg', 7727, 'IMG', 'USER_AVATAR', '2', 'avatar', 2, 0, 1, '2025-09-15 14:08:09', '2025-09-16 14:08:09');
INSERT INTO `sys_file_info` VALUES (19, 'ima9897ges.jpg', '/files/bussiness/user_avatar/1757916661296.jpg', 4977, 'IMG', 'USER_AVATAR', '2', 'avatar', 2, 0, 1, '2025-09-15 14:11:01', NULL);
INSERT INTO `sys_file_info` VALUES (20, 'Untitl777ed.jpg', '/files/bussiness/user_avatar/1757916687452.jpg', 7598, 'IMG', 'USER_AVATAR', '2', 'avatar', 2, 0, 1, '2025-09-15 14:11:27', NULL);
INSERT INTO `sys_file_info` VALUES (21, 'ima22ges.jpg', '/files/bussiness/snack_cover/1757917331578.jpg', 20114, 'IMG', 'SNACK_COVER', '0', 'coverImage', 1, 0, 1, '2025-09-15 14:22:12', NULL);
INSERT INTO `sys_file_info` VALUES (22, '111.jpg', '/files/bussiness/snack_detail/1757917339188.jpg', 5193, 'IMG', 'SNACK_DETAIL', '0', 'detailImages', 1, 0, 1, '2025-09-15 14:22:19', NULL);
INSERT INTO `sys_file_info` VALUES (23, 'ima22ges.jpg', '/files/bussiness/snack_detail/1757917344888.jpg', 20114, 'IMG', 'SNACK_DETAIL', '0', 'detailImages', 1, 0, 1, '2025-09-15 14:22:25', NULL);
INSERT INTO `sys_file_info` VALUES (24, 'COVER.jpg', '/files/bussiness/snack_detail/1757917363412.jpg', 237854, 'IMG', 'SNACK_DETAIL', '0', 'detailImages', 1, 0, 1, '2025-09-15 14:22:43', NULL);
INSERT INTO `sys_file_info` VALUES (25, '111.jpg', '/files/bussiness/snack_cover/1757917577432.jpg', 5193, 'IMG', 'SNACK_COVER', '1', 'coverImage', 1, 0, 0, '2025-09-15 14:26:17', NULL);
INSERT INTO `sys_file_info` VALUES (26, 'ima22ges.jpg', '/files/bussiness/snack_cover/1757917592785.jpg', 20114, 'IMG', 'SNACK_COVER', '1', 'coverImage', 1, 0, 0, '2025-09-15 14:26:33', NULL);
INSERT INTO `sys_file_info` VALUES (27, '111.jpg', '/files/bussiness/snack_cover/1757917659396.jpg', 5193, 'IMG', 'SNACK_COVER', '1', 'coverImage', 1, 0, 1, '2025-09-15 14:27:39', NULL);
INSERT INTO `sys_file_info` VALUES (28, 'ima22ges.jpg', '/files/bussiness/snack_detail/1757917674508.jpg', 20114, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:27:55', NULL);
INSERT INTO `sys_file_info` VALUES (29, 'COVER.jpg', '/files/bussiness/snack_detail/1757917674508.jpg', 237854, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:27:55', NULL);
INSERT INTO `sys_file_info` VALUES (30, '111.jpg', '/files/bussiness/snack_detail/1757917674508.jpg', 5193, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:27:55', NULL);
INSERT INTO `sys_file_info` VALUES (31, 'ima22ges.jpg', '/files/bussiness/snack_detail/1757917682650.jpg', 20114, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:28:03', NULL);
INSERT INTO `sys_file_info` VALUES (32, 'COVER.jpg', '/files/bussiness/snack_detail/1757917684680.jpg', 237854, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:28:05', NULL);
INSERT INTO `sys_file_info` VALUES (33, 'ima22ges.jpg', '/files/bussiness/snack_detail/1757918047145.jpg', 20114, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:34:07', NULL);
INSERT INTO `sys_file_info` VALUES (34, '111.jpg', '/files/bussiness/snack_detail/1757918047149.jpg', 5193, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:34:07', NULL);
INSERT INTO `sys_file_info` VALUES (35, 'COVER.jpg', '/files/bussiness/snack_detail/1757918047153.jpg', 237854, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 0, '2025-09-15 14:34:07', NULL);
INSERT INTO `sys_file_info` VALUES (36, 'ima22ges.jpg', '/files/bussiness/snack_detail/1757918107494.jpg', 20114, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 1, '2025-09-15 14:35:07', NULL);
INSERT INTO `sys_file_info` VALUES (37, 'COVER.jpg', '/files/bussiness/snack_detail/1757918107563.jpg', 237854, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 1, '2025-09-15 14:35:08', NULL);
INSERT INTO `sys_file_info` VALUES (38, '111.jpg', '/files/bussiness/snack_detail/1757918107570.jpg', 5193, 'IMG', 'SNACK_DETAIL', '1', 'detailImages', 1, 0, 1, '2025-09-15 14:35:08', NULL);
INSERT INTO `sys_file_info` VALUES (39, 'ima323ges.jpg', '/files/bussiness/snack_cover/1757924023959.jpg', 11629, 'IMG', 'SNACK_COVER', '2', 'coverImage', 1, 0, 1, '2025-09-15 16:13:44', NULL);
INSERT INTO `sys_file_info` VALUES (40, 'ima323ges.jpg', '/files/bussiness/snack_detail/1757924028397.jpg', 11629, 'IMG', 'SNACK_DETAIL', '2', 'detailImages', 1, 0, 0, '2025-09-15 16:13:48', NULL);
INSERT INTO `sys_file_info` VALUES (41, 'imag2es.jpg', '/files/bussiness/snack_detail/1757924028417.jpg', 9171, 'IMG', 'SNACK_DETAIL', '2', 'detailImages', 1, 0, 0, '2025-09-15 16:13:48', NULL);
INSERT INTO `sys_file_info` VALUES (42, 'ima323ges.jpg', '/files/bussiness/snack_detail/1757924040182.jpg', 11629, 'IMG', 'SNACK_DETAIL', '2', 'detailImages', 1, 0, 1, '2025-09-15 16:14:00', NULL);
INSERT INTO `sys_file_info` VALUES (43, 'imaqwes.jpg', '/files/bussiness/snack_cover/1757924047587.jpg', 15460, 'IMG', 'SNACK_COVER', '3', 'coverImage', 1, 0, 1, '2025-09-15 16:14:08', NULL);
INSERT INTO `sys_file_info` VALUES (44, 'imaqwes.jpg', '/files/bussiness/snack_detail/1757924049222.jpg', 15460, 'IMG', 'SNACK_DETAIL', '3', 'detailImages', 1, 0, 0, '2025-09-15 16:14:09', NULL);
INSERT INTO `sys_file_info` VALUES (45, 'im2112ages.jpg', '/files/bussiness/snack_detail/1757924049222.jpg', 12644, 'IMG', 'SNACK_DETAIL', '3', 'detailImages', 1, 0, 0, '2025-09-15 16:14:09', NULL);
INSERT INTO `sys_file_info` VALUES (46, 'imaqwes.jpg', '/files/bussiness/snack_detail/1757924055947.jpg', 15460, 'IMG', 'SNACK_DETAIL', '3', 'detailImages', 1, 0, 1, '2025-09-15 16:14:16', NULL);
INSERT INTO `sys_file_info` VALUES (47, 'imag2322es.jpg', '/files/bussiness/snack_cover/1757924066405.jpg', 8966, 'IMG', 'SNACK_COVER', '4', 'coverImage', 1, 0, 1, '2025-09-15 16:14:26', NULL);
INSERT INTO `sys_file_info` VALUES (48, 'imag2322es.jpg', '/files/bussiness/snack_detail/1757924067839.jpg', 8966, 'IMG', 'SNACK_DETAIL', '4', 'detailImages', 1, 0, 0, '2025-09-15 16:14:28', NULL);
INSERT INTO `sys_file_info` VALUES (49, 'ima32323232ges.jpg', '/files/bussiness/snack_detail/1757924067839.jpg', 7749, 'IMG', 'SNACK_DETAIL', '4', 'detailImages', 1, 0, 0, '2025-09-15 16:14:28', NULL);
INSERT INTO `sys_file_info` VALUES (50, 'imag2322es.jpg', '/files/bussiness/snack_detail/1757924073299.jpg', 8966, 'IMG', 'SNACK_DETAIL', '4', 'detailImages', 1, 0, 1, '2025-09-15 16:14:33', NULL);
INSERT INTO `sys_file_info` VALUES (51, 'im6543ages.jpg', '/files/bussiness/snack_cover/1757924084842.jpg', 10921, 'IMG', 'SNACK_COVER', '5', 'coverImage', 1, 0, 1, '2025-09-15 16:14:45', NULL);
INSERT INTO `sys_file_info` VALUES (52, 'image5432s.jpg', '/files/bussiness/snack_detail/1757924086424.jpg', 5482, 'IMG', 'SNACK_DETAIL', '5', 'detailImages', 1, 0, 0, '2025-09-15 16:14:46', NULL);
INSERT INTO `sys_file_info` VALUES (53, 'im6543ages.jpg', '/files/bussiness/snack_detail/1757924086424.jpg', 10921, 'IMG', 'SNACK_DETAIL', '5', 'detailImages', 1, 0, 0, '2025-09-15 16:14:46', NULL);
INSERT INTO `sys_file_info` VALUES (54, 'image5432s.jpg', '/files/bussiness/snack_detail/1757924092549.jpg', 5482, 'IMG', 'SNACK_DETAIL', '5', 'detailImages', 1, 0, 1, '2025-09-15 16:14:53', NULL);
INSERT INTO `sys_file_info` VALUES (55, 'Skittles-Loui1siana-2003.jpg', '/files/bussiness/snack_cover/1757924099707.jpg', 785075, 'IMG', 'SNACK_COVER', '6', 'coverImage', 1, 0, 1, '2025-09-15 16:15:00', NULL);
INSERT INTO `sys_file_info` VALUES (56, 'wKgDiV6qatOACrHyAAQlRWRpNlI618.jpg_800x800.jpg', '/files/bussiness/snack_detail/1757924101369.jpg', 271685, 'IMG', 'SNACK_DETAIL', '6', 'detailImages', 1, 0, 0, '2025-09-15 16:15:01', NULL);
INSERT INTO `sys_file_info` VALUES (57, 'Skittles-Loui1siana-2003.jpg', '/files/bussiness/snack_detail/1757924104363.jpg', 785075, 'IMG', 'SNACK_DETAIL', '6', 'detailImages', 1, 0, 1, '2025-09-15 16:15:04', NULL);
INSERT INTO `sys_file_info` VALUES (58, '5c1553625b4749f265b9895cfb3952bc.jpg', '/files/bussiness/snack_cover/1757924110884.jpg', 115342, 'IMG', 'SNACK_COVER', '7', 'coverImage', 1, 0, 1, '2025-09-15 16:15:11', NULL);
INSERT INTO `sys_file_info` VALUES (59, 'imag221212es.jpg', '/files/bussiness/snack_detail/1757924114885.jpg', 7977, 'IMG', 'SNACK_DETAIL', '7', 'detailImages', 1, 0, 0, '2025-09-15 16:15:15', NULL);
INSERT INTO `sys_file_info` VALUES (60, '5c1553625b4749f265b9895cfb3952bc.jpg', '/files/bussiness/snack_detail/1757924116938.jpg', 115342, 'IMG', 'SNACK_DETAIL', '7', 'detailImages', 1, 0, 1, '2025-09-15 16:15:17', NULL);
INSERT INTO `sys_file_info` VALUES (61, 'image65423s.jpg', '/files/bussiness/snack_cover/1757924124435.jpg', 12284, 'IMG', 'SNACK_COVER', '8', 'coverImage', 1, 0, 1, '2025-09-15 16:15:24', NULL);
INSERT INTO `sys_file_info` VALUES (62, 'ima443434ges.jpg', '/files/bussiness/snack_detail/1757924125159.jpg', 14676, 'IMG', 'SNACK_DETAIL', '8', 'detailImages', 1, 0, 0, '2025-09-15 16:15:25', NULL);
INSERT INTO `sys_file_info` VALUES (63, 'image65423s.jpg', '/files/bussiness/snack_detail/1757924128107.jpg', 12284, 'IMG', 'SNACK_DETAIL', '8', 'detailImages', 1, 0, 1, '2025-09-15 16:15:28', NULL);
INSERT INTO `sys_file_info` VALUES (64, 'imaqwes.jpg', '/files/bussiness/comment_image/1757995338328.jpg', 15460, 'IMG', 'COMMENT_IMAGE', '1', 'images', 2, 0, 1, '2025-09-16 12:02:18', NULL);
INSERT INTO `sys_file_info` VALUES (65, '【哲风壁纸】卡通-派大星-海.png', '/files/bussiness/carousel_image/1758004409781.png', 1819463, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:33:30', NULL);
INSERT INTO `sys_file_info` VALUES (66, 'COVER.jpg', '/files/bussiness/carousel_image/1758004793046.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:39:53', NULL);
INSERT INTO `sys_file_info` VALUES (67, 'COVER.jpg', '/files/bussiness/carousel_image/1758004949850.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:42:30', NULL);
INSERT INTO `sys_file_info` VALUES (68, 'COVER.jpg', '/files/bussiness/carousel_image/1758005077421.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:44:37', NULL);
INSERT INTO `sys_file_info` VALUES (69, 'COVER.jpg', '/files/bussiness/carousel_image/1758005266840.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:47:47', NULL);
INSERT INTO `sys_file_info` VALUES (70, 'COVER.jpg', '/files/bussiness/carousel_image/1758005280434.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:48:00', NULL);
INSERT INTO `sys_file_info` VALUES (71, 'COVER.jpg', '/files/bussiness/carousel_image/1758005295778.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:48:16', NULL);
INSERT INTO `sys_file_info` VALUES (72, 'COVER.jpg', '/files/bussiness/carousel_image/1758005321446.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:48:41', NULL);
INSERT INTO `sys_file_info` VALUES (73, 'COVER.jpg', '/files/bussiness/carousel_image/1758005390882.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', NULL, 1, 1, '2025-09-16 14:49:51', NULL);
INSERT INTO `sys_file_info` VALUES (74, 'COVER.jpg', '/files/bussiness/carousel_image/1758005650616.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:54:11', NULL);
INSERT INTO `sys_file_info` VALUES (75, 'COVER.jpg', '/files/bussiness/carousel_image/1758005705475.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:55:05', NULL);
INSERT INTO `sys_file_info` VALUES (76, 'COVER.jpg', '/files/bussiness/carousel_image/1758005721086.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:55:21', NULL);
INSERT INTO `sys_file_info` VALUES (77, 'COVER.jpg', '/files/bussiness/carousel_image/1758005788209.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:56:28', NULL);
INSERT INTO `sys_file_info` VALUES (78, 'COVER.jpg', '/files/bussiness/carousel_image/1758005826695.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:57:07', NULL);
INSERT INTO `sys_file_info` VALUES (79, 'COVER.jpg', '/files/bussiness/carousel_image/1758005967502.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 14:59:28', NULL);
INSERT INTO `sys_file_info` VALUES (80, 'image5432s.jpg', '/files/bussiness/carousel_image/1758006072040.jpg', 5482, 'IMG', 'CAROUSEL_IMAGE', '0', 'image', 1, 1, 1, '2025-09-16 15:01:12', NULL);
INSERT INTO `sys_file_info` VALUES (81, '【哲风壁纸】卡通-派大星-海.png', '/files/bussiness/carousel_image/1758006103812.png', 1819463, 'IMG', 'CAROUSEL_IMAGE', '1', 'image', 1, 0, 0, '2025-09-16 15:01:44', NULL);
INSERT INTO `sys_file_info` VALUES (82, 'COVER.jpg', '/files/bussiness/carousel_image/1758006398228.jpg', 237854, 'IMG', 'CAROUSEL_IMAGE', '1', 'image', 1, 0, 1, '2025-09-16 15:06:38', NULL);
INSERT INTO `sys_file_info` VALUES (83, 'Untitl888ed.jpg', '/files/bussiness/user_avatar/1758010605687.jpg', 6017, 'IMG', 'USER_AVATAR', '1', 'avatar', 1, 0, 1, '2025-09-16 16:16:46', NULL);

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `consignee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区/县',
  `detailed_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为默认地址（1是，0否）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, 2, 'jx', '15252393509', 'ttt', 'ttt', 'ttt', 'tt111111111111', 0, '2025-09-15 13:31:06', '2025-09-16 11:16:10');
INSERT INTO `t_address` VALUES (2, 2, 'ces111', '13123456789', '881', '51', '5156156', '1615616', 1, '2025-09-15 13:34:27', '2025-09-16 11:16:10');

-- ----------------------------
-- Table structure for t_carousel
-- ----------------------------
DROP TABLE IF EXISTS `t_carousel`;
CREATE TABLE `t_carousel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题（可选，用于后台标识）',
  `jump_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'URL' COMMENT '跳转类型：URL/PRODUCT/NONE',
  `jump_target` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转目标：当为URL时存放链接；为PRODUCT时存放商品ID',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
  `start_time` datetime NULL DEFAULT NULL COMMENT '生效开始时间（可选）',
  `end_time` datetime NULL DEFAULT NULL COMMENT '生效结束时间（可选）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_carousel_status`(`status` ASC) USING BTREE,
  INDEX `idx_carousel_sort`(`sort_order` ASC) USING BTREE,
  INDEX `idx_carousel_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_carousel
-- ----------------------------
INSERT INTO `t_carousel` VALUES (1, '', 'PRODUCT', '1', 0, 'ENABLED', NULL, NULL, '2025-09-16 15:01:48', '2025-09-16 15:01:48');

-- ----------------------------
-- Table structure for t_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `snack_id` bigint NOT NULL COMMENT '零食ID',
  `quantity` int NOT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_snack`(`user_id` ASC, `snack_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_snack_id`(`snack_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_snack` FOREIGN KEY (`snack_id`) REFERENCES `t_snack` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart
-- ----------------------------
INSERT INTO `t_cart` VALUES (3, 2, 7, 1, '2025-09-16 16:42:16', '2025-09-16 16:42:16');
INSERT INTO `t_cart` VALUES (4, 2, 6, 1, '2025-09-16 16:42:16', '2025-09-16 16:42:16');

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE, INACTIVE）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '零食分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_category
-- ----------------------------
INSERT INTO `t_category` VALUES (1, '膨化食品', 1, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:40:29');
INSERT INTO `t_category` VALUES (2, '坚果炒货', 2, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (3, '糖果巧克力', 3, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (4, '饼干糕点', 4, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (5, '果脯蜜饯', 5, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (6, '肉类零食', 6, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (7, '海味零食', 7, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');
INSERT INTO `t_category` VALUES (8, '传统糕点', 8, 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 10:28:26');

-- ----------------------------
-- Table structure for t_favorite
-- ----------------------------
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `snack_id` bigint NOT NULL COMMENT '零食ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_snack`(`user_id` ASC, `snack_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_snack_id`(`snack_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_snack` FOREIGN KEY (`snack_id`) REFERENCES `t_snack` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_favorite
-- ----------------------------
INSERT INTO `t_favorite` VALUES (3, 2, 5, '2025-09-16 10:07:10');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `address_id` bigint NOT NULL COMMENT '收货地址ID',
  `total_amount` int NOT NULL COMMENT '订单总金额（单位：分）',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNPAID' COMMENT '订单状态（UNPAID, PAID, SHIPPED, COMPLETED, CANCELLED）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `fk_order_address`(`address_id` ASC) USING BTREE,
  INDEX `idx_order_payment_time`(`payment_time` ASC) USING BTREE,
  INDEX `idx_order_ship_time`(`ship_time` ASC) USING BTREE,
  CONSTRAINT `fk_order_address` FOREIGN KEY (`address_id`) REFERENCES `t_address` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (1, 'ORDER17579142765495E2D6F', 2, 1, 1199, 'COMPLETED', 'ces1', '2025-09-15 13:31:17', '2025-09-15 13:32:06', '2025-09-15 13:38:13', '2025-09-15 13:38:28', NULL);
INSERT INTO `t_order` VALUES (2, 'ORDER1758010574163D99D6A', 2, 2, 2398, 'PAID', NULL, '2025-09-16 16:16:14', '2025-09-16 16:16:17', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for t_order_item
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `snack_id` bigint NOT NULL COMMENT '零食ID',
  `quantity` int NOT NULL COMMENT '购买数量',
  `price` int NOT NULL COMMENT '购买时单价（单位：分）',
  `snack_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '零食名称（冗余）',
  `snack_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '零食图片（冗余）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_snack_id`(`snack_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_snack` FOREIGN KEY (`snack_id`) REFERENCES `t_snack` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES (1, 1, 8, 1, 1199, '曲奇饼干', NULL, '2025-09-15 13:31:17');
INSERT INTO `t_order_item` VALUES (2, 2, 8, 2, 1199, '曲奇饼干', '/files/bussiness/snack_cover/1757924124435.jpg', '2025-09-16 16:16:14');

-- ----------------------------
-- Table structure for t_review
-- ----------------------------
DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `snack_id` bigint NOT NULL COMMENT '零食ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` int NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review_snack`(`snack_id` ASC) USING BTREE,
  INDEX `idx_review_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '零食评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_review
-- ----------------------------

-- ----------------------------
-- Table structure for t_snack
-- ----------------------------
DROP TABLE IF EXISTS `t_snack`;
CREATE TABLE `t_snack`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '零食名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '零食描述',
  `price` int NOT NULL COMMENT '价格（单位：分）',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `detail_images` json NULL COMMENT '详情图片URL列表 (JSON数组)',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ON_SALE' COMMENT '状态（ON_SALE, OFF_SHELF）',
  `sales_count` int NOT NULL DEFAULT 0 COMMENT '销售数量',
  `favorite_count` int NOT NULL DEFAULT 0 COMMENT '收藏数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_price`(`price` ASC) USING BTREE,
  INDEX `idx_sales_count`(`sales_count` ASC) USING BTREE,
  INDEX `idx_snack_name`(`name` ASC) USING BTREE,
  INDEX `idx_snack_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_snack_favorite_count`(`favorite_count` ASC) USING BTREE,
  CONSTRAINT `fk_snack_category` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '零食表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_snack
-- ----------------------------
INSERT INTO `t_snack` VALUES (1, 1, '薯片原味', '香脆可口的原味薯片，精选优质土豆制作', 599, 99, '/files/bussiness/snack_cover/1757917659396.jpg', '[\"/files/bussiness/snack_detail/1757918107494.jpg\", \"/files/bussiness/snack_detail/1757918107563.jpg\", \"/files/bussiness/snack_detail/1757918107570.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-16 10:43:25');
INSERT INTO `t_snack` VALUES (2, 1, '爆米花焦糖味', '香甜可口的焦糖味爆米花，休闲必备', 899, 80, '/files/bussiness/snack_cover/1757924023959.jpg', '[\"/files/bussiness/snack_detail/1757924028417.jpg\", \"/files/bussiness/snack_detail/1757924040182.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-15 16:14:00');
INSERT INTO `t_snack` VALUES (3, 2, '混合坚果', '精选核桃、杏仁、腰果等多种坚果', 1299, 50, '/files/bussiness/snack_cover/1757924047587.jpg', '[\"/files/bussiness/snack_detail/1757924049222.jpg\", \"/files/bussiness/snack_detail/1757924055947.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-16 10:31:04');
INSERT INTO `t_snack` VALUES (4, 2, '瓜子五香味', '传统五香味瓜子，闲暇时光的好伴侣', 799, 120, '/files/bussiness/snack_cover/1757924066405.jpg', '[\"/files/bussiness/snack_detail/1757924067839.jpg\", \"/files/bussiness/snack_detail/1757924073299.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-15 16:14:33');
INSERT INTO `t_snack` VALUES (5, 3, '牛奶巧克力', '浓郁香甜的牛奶巧克力，丝滑口感', 1599, 60, '/files/bussiness/snack_cover/1757924084842.jpg', '[\"/files/bussiness/snack_detail/1757924086424.jpg\", \"/files/bussiness/snack_detail/1757924092549.jpg\"]', 'ON_SALE', 0, 1, '2025-09-15 10:28:26', '2025-09-16 10:07:10');
INSERT INTO `t_snack` VALUES (6, 3, '彩虹糖', '七彩缤纷的水果味糖果，酸甜可口', 699, 90, '/files/bussiness/snack_cover/1757924099707.jpg', '[\"/files/bussiness/snack_detail/1757924101369.jpg\", \"/files/bussiness/snack_detail/1757924104363.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-15 16:15:07');
INSERT INTO `t_snack` VALUES (7, 4, '奥利奥饼干', '经典巧克力夹心饼干，香浓美味', 999, 75, '/files/bussiness/snack_cover/1757924110884.jpg', '[\"/files/bussiness/snack_detail/1757924114885.jpg\", \"/files/bussiness/snack_detail/1757924116938.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-15 16:15:17');
INSERT INTO `t_snack` VALUES (8, 4, '曲奇饼干', '酥脆香甜的黄油曲奇饼干', 1199, 37, '/files/bussiness/snack_cover/1757924124435.jpg', '[\"/files/bussiness/snack_detail/1757924125159.jpg\", \"/files/bussiness/snack_detail/1757924128107.jpg\"]', 'ON_SALE', 0, 0, '2025-09-15 10:28:26', '2025-09-16 16:16:19');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密后）',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '用户类型（ADMIN, USER）',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态（ACTIVE, INACTIVE）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_user_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', '系统管理员', '/files/bussiness/user_avatar/1758010605687.jpg', NULL, 'ADMIN', 'ACTIVE', '2025-09-15 10:28:26', '2025-09-16 16:16:48');
INSERT INTO `t_user` VALUES (2, 'testuser', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', '测试用户', '/files/bussiness/user_avatar/1757916687452.jpg', NULL, 'USER', 'ACTIVE', '2025-09-15 10:28:26', '2025-09-15 14:11:28');

-- ----------------------------
-- View structure for v_order_detail
-- ----------------------------
DROP VIEW IF EXISTS `v_order_detail`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_order_detail` AS select `o`.`id` AS `id`,`o`.`order_no` AS `order_no`,`o`.`total_amount` AS `total_amount`,`o`.`status` AS `status`,`o`.`remark` AS `remark`,`o`.`create_time` AS `create_time`,`o`.`payment_time` AS `payment_time`,`o`.`ship_time` AS `ship_time`,`o`.`complete_time` AS `complete_time`,`u`.`id` AS `user_id`,`u`.`username` AS `username`,`u`.`nickname` AS `nickname`,`a`.`consignee_name` AS `consignee_name`,`a`.`phone` AS `phone`,concat(`a`.`province`,`a`.`city`,`a`.`district`,`a`.`detailed_address`) AS `full_address` from ((`t_order` `o` left join `t_user` `u` on((`o`.`user_id` = `u`.`id`))) left join `t_address` `a` on((`o`.`address_id` = `a`.`id`)));

-- ----------------------------
-- View structure for v_snack_detail
-- ----------------------------
DROP VIEW IF EXISTS `v_snack_detail`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_snack_detail` AS select `s`.`id` AS `id`,`s`.`name` AS `name`,`s`.`description` AS `description`,`s`.`price` AS `price`,`s`.`stock` AS `stock`,`s`.`cover_image` AS `cover_image`,`s`.`detail_images` AS `detail_images`,`s`.`status` AS `status`,`s`.`sales_count` AS `sales_count`,`s`.`create_time` AS `create_time`,`s`.`update_time` AS `update_time`,`c`.`id` AS `category_id`,`c`.`name` AS `category_name` from (`t_snack` `s` left join `t_category` `c` on((`s`.`category_id` = `c`.`id`)));

-- ----------------------------
-- View structure for v_user_favorite
-- ----------------------------
DROP VIEW IF EXISTS `v_user_favorite`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_user_favorite` AS select `f`.`id` AS `favorite_id`,`f`.`user_id` AS `user_id`,`f`.`create_time` AS `favorite_time`,`s`.`id` AS `snack_id`,`s`.`name` AS `snack_name`,`s`.`description` AS `snack_description`,`s`.`price` AS `price`,`s`.`stock` AS `stock`,`s`.`cover_image` AS `cover_image`,`s`.`status` AS `status`,`s`.`sales_count` AS `sales_count`,`s`.`favorite_count` AS `favorite_count`,`c`.`id` AS `category_id`,`c`.`name` AS `category_name` from ((`t_favorite` `f` left join `t_snack` `s` on((`f`.`snack_id` = `s`.`id`))) left join `t_category` `c` on((`s`.`category_id` = `c`.`id`))) where (`s`.`status` = 'ON_SALE');

-- ----------------------------
-- Triggers structure for table t_favorite
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_favorite_insert`;
delimiter ;;
CREATE TRIGGER `tr_favorite_insert` AFTER INSERT ON `t_favorite` FOR EACH ROW BEGIN
    UPDATE `t_snack` SET `favorite_count` = `favorite_count` + 1 WHERE `id` = NEW.`snack_id`;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table t_favorite
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_favorite_delete`;
delimiter ;;
CREATE TRIGGER `tr_favorite_delete` AFTER DELETE ON `t_favorite` FOR EACH ROW BEGIN
    UPDATE `t_snack` SET `favorite_count` = `favorite_count` - 1 WHERE `id` = OLD.`snack_id`;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
