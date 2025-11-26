-- 评价模块建表脚本
-- 表：t_review 用于存储零食的图文评价（文本+评分），图片通过文件服务(SysFileInfo)按业务类型COMMENT_IMAGE关联

DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `snack_id` BIGINT NOT NULL COMMENT '零食ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `rating` INT NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
  `content` VARCHAR(1000) NOT NULL COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_snack` (`snack_id`),
  KEY `idx_review_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零食评价表';


