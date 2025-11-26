-- ======================================
-- 商品收藏模块数据库脚本
-- 版本: 1.0
-- 创建日期: 2025-01-16
-- 说明: 商品收藏功能相关表结构
-- ======================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ======================================
-- 收藏表 (t_favorite)
-- ======================================
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `snack_id` bigint(20) NOT NULL COMMENT '零食ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_snack` (`user_id`, `snack_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_snack_id` (`snack_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_snack` FOREIGN KEY (`snack_id`) REFERENCES `t_snack` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏表';

-- ======================================
-- 为零食表添加收藏统计字段
-- ======================================
ALTER TABLE `t_snack` ADD COLUMN `favorite_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数量' AFTER `sales_count`;

-- 创建收藏统计索引
CREATE INDEX `idx_snack_favorite_count` ON `t_snack` (`favorite_count`);

-- ======================================
-- 创建收藏相关视图
-- ======================================

-- 创建用户收藏详情视图
CREATE VIEW `v_user_favorite` AS
SELECT 
    f.`id` AS `favorite_id`,
    f.`user_id`,
    f.`create_time` AS `favorite_time`,
    s.`id` AS `snack_id`,
    s.`name` AS `snack_name`,
    s.`description` AS `snack_description`,
    s.`price`,
    s.`stock`,
    s.`cover_image`,
    s.`status`,
    s.`sales_count`,
    s.`favorite_count`,
    c.`id` AS `category_id`,
    c.`name` AS `category_name`
FROM `t_favorite` f
LEFT JOIN `t_snack` s ON f.`snack_id` = s.`id`
LEFT JOIN `t_category` c ON s.`category_id` = c.`id`
WHERE s.`status` = 'ON_SALE';

-- ======================================
-- 创建触发器维护收藏统计
-- ======================================

-- 收藏新增时更新统计
DELIMITER ;;
CREATE TRIGGER `tr_favorite_insert` AFTER INSERT ON `t_favorite` FOR EACH ROW
BEGIN
    UPDATE `t_snack` SET `favorite_count` = `favorite_count` + 1 WHERE `id` = NEW.`snack_id`;
END;;

-- 收藏删除时更新统计
CREATE TRIGGER `tr_favorite_delete` AFTER DELETE ON `t_favorite` FOR EACH ROW
BEGIN
    UPDATE `t_snack` SET `favorite_count` = `favorite_count` - 1 WHERE `id` = OLD.`snack_id`;
END;;
DELIMITER ;

-- ======================================
-- 插入测试数据（可选）
-- ======================================

-- 为测试用户添加一些收藏记录
INSERT INTO `t_favorite` (`user_id`, `snack_id`) VALUES
(2, 1),  -- 测试用户收藏薯片原味
(2, 3),  -- 测试用户收藏混合坚果
(2, 5);  -- 测试用户收藏牛奶巧克力

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================
-- 脚本执行完成提示
-- ======================================
SELECT '商品收藏模块数据库初始化完成！' AS '提示';
SELECT COUNT(*) AS '收藏记录数量' FROM `t_favorite`;
