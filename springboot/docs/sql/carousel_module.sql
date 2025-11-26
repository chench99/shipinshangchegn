-- 轮播图模块表结构（简易版）
-- 表：t_carousel
-- 说明：存储首页/频道轮播图的元信息；图片文件通过文件中心关联（业务类型：CAROUSEL_IMAGE，businessId 为 carousel.id）

DROP TABLE IF EXISTS `t_carousel`;
CREATE TABLE `t_carousel` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(255) NULL COMMENT '标题（可选，用于后台标识）',
  `jump_type` VARCHAR(32) DEFAULT 'URL' COMMENT '跳转类型：URL/PRODUCT/NONE',
  `jump_target` VARCHAR(512) NULL COMMENT '跳转目标：当为URL时存放链接；为PRODUCT时存放商品ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
  `start_time` DATETIME NULL COMMENT '生效开始时间（可选）',
  `end_time` DATETIME NULL COMMENT '生效结束时间（可选）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='轮播图表';

-- 索引
CREATE INDEX idx_carousel_status ON `t_carousel`(`status`);
CREATE INDEX idx_carousel_sort ON `t_carousel`(`sort_order`);
CREATE INDEX idx_carousel_time ON `t_carousel`(`start_time`, `end_time`);

-- 备注：图片上传使用两阶段提交（策略A）
-- 1) POST /file/upload/temp-business (businessType=CAROUSEL_IMAGE, businessField='image')
-- 2) 创建/更新轮播后，PUT /file/confirm/{tempFileId} 绑定到 businessId=轮播ID

