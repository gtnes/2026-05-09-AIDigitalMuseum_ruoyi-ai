-- ============================================================
-- AI视频模块：
--   ai_video          AI视频表（标题/封面/视频/描述/预设问题等）
--     category_id      所属分类（仅内部筛选，不对外）
--     show_category    展示类别（对外展示：宣传片/文物/历史等，
--                      由字典 ai_video_show_category 维护）
--   ai_video_category AI视频分类表（内部筛选分类，
--                      AI视频模块中下拉选择）
--   菜单：AI视频管理、AI视频分类（挂"云山AI"目录 2089647684201230337 下，
--         紧跟"平台音色库"菜单之后）
-- 本脚本幂等，可重复执行（CREATE IF NOT EXISTS + INSERT IGNORE）。
-- menu_id 使用 2099010600000000xxx 段，与已有 2026-07-24（...010000...）、
-- 2026-09-04（...040000...）、2026-09-14（...050000...）脚本不冲突。
-- 若已执行过不含 show_category 列的旧版脚本，
-- 请改执行 2026-09-22-ai-video-show-category.sql 补列。
-- ============================================================

-- ============================================================
-- 1. AI视频分类表
-- ============================================================
CREATE TABLE IF NOT EXISTS `ai_video_category` (
    `id` bigint NOT NULL COMMENT '主键',
    `category_name` varchar(64) NOT NULL COMMENT '分类名称',
    `status` char(1) NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `sort` int NULL DEFAULT 0 COMMENT '显示顺序',
    `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户Id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_avc_category_name` (`category_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI视频分类' ROW_FORMAT = DYNAMIC;

-- ============================================================
-- 2. AI视频表
-- ============================================================
CREATE TABLE IF NOT EXISTS `ai_video` (
    `id` bigint NOT NULL COMMENT '主键',
    `title` varchar(128) NOT NULL COMMENT '标题',
    `cover_url` varchar(500) NULL DEFAULT NULL COMMENT '封面图片URL',
    `video_url` varchar(500) NOT NULL COMMENT '视频文件URL',
    `category_id` bigint NULL DEFAULT NULL COMMENT '所属分类ID(ai_video_category.id，仅内部筛选用，不对外)',
    `show_category` varchar(50) NULL DEFAULT NULL COMMENT '展示类别(对外展示：宣传片/文物/历史等，字典 ai_video_show_category)',
    `description` varchar(1000) NULL DEFAULT NULL COMMENT '描述',
    `preset_questions` json NULL COMMENT '预设问题列表（JSON数组）',
    `duration` int NULL DEFAULT NULL COMMENT '视频时长(秒)',
    `play_count` bigint NULL DEFAULT 0 COMMENT '播放次数',
    `sort` int NULL DEFAULT 0 COMMENT '显示顺序',
    `status` char(1) NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户Id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_av_category_id` (`category_id`) USING BTREE,
    INDEX `idx_av_title` (`title`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI视频' ROW_FORMAT = DYNAMIC;

-- ============================================================
-- 3. 菜单：AI视频管理 + AI视频分类
--    父菜单=云山AI目录(2089647684201230337)，紧跟"平台音色库"菜单之后；
--    AI博物馆(2099010400000000001)排序后移至6，为视频菜单让位。
-- ============================================================
INSERT IGNORE INTO `sys_menu` VALUES
-- AI视频管理
(2099010600000000001, 'AI视频管理', 2089647684201230337, 4, 'ai-video', 'video/video/index', NULL, 1, 0, 'C', '0', '0', 'video:video:list', 'ant-design:video-camera-outlined', 103, 1, '2026-09-22 00:00:00', NULL, NULL, 'AI视频管理菜单'),
(2099010600000000002, 'AI视频管理查询', 2099010600000000001, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:video:query', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000003, 'AI视频管理新增', 2099010600000000001, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:video:add', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000004, 'AI视频管理修改', 2099010600000000001, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:video:edit', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000005, 'AI视频管理删除', 2099010600000000001, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:video:remove', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000006, 'AI视频管理导出', 2099010600000000001, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:video:export', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
-- AI视频分类
(2099010600000000011, 'AI视频分类', 2089647684201230337, 5, 'video-category', 'video/category/index', NULL, 1, 0, 'C', '0', '0', 'video:category:list', 'ant-design:tags-outlined', 103, 1, '2026-09-22 00:00:00', NULL, NULL, 'AI视频分类管理菜单'),
(2099010600000000012, 'AI视频分类查询', 2099010600000000011, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:category:query', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000013, 'AI视频分类新增', 2099010600000000011, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:category:add', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000014, 'AI视频分类修改', 2099010600000000011, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:category:edit', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000015, 'AI视频分类删除', 2099010600000000011, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:category:remove', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, ''),
(2099010600000000016, 'AI视频分类导出', 2099010600000000011, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'video:category:export', '#', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '');

-- ============================================================
-- 4. 菜单位置维护（幂等，可重复执行）
--    AI博物馆排序后移至6，为视频菜单让位。
-- ============================================================
UPDATE `sys_menu` SET `order_num` = 6 WHERE `menu_id` = 2099010400000000001 AND `parent_id` = 2089647684201230337;

-- ============================================================
-- 5. 展示类别字典（对外展示维度：宣传片/文物/历史等，
--    可在"系统管理-字典管理"中随时增删类别，字典值即中文名，
--    前台可直接展示/筛选，无需翻译。幂等 INSERT IGNORE。）
-- ============================================================
INSERT IGNORE INTO `sys_dict_type` VALUES (2099010600000001001, '000000', 'AI视频展示类别', 'ai_video_show_category', 103, 1, '2026-09-22 00:00:00', NULL, NULL, 'AI视频前台展示类别（宣传片/文物/历史等）');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001011, '000000', 1, '宣传片', '宣传片', 'ai_video_show_category', '', 'primary', 'Y', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '宣传片');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001012, '000000', 2, '文物', '文物', 'ai_video_show_category', '', 'success', 'N', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '文物');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001013, '000000', 3, '历史', '历史', 'ai_video_show_category', '', 'warning', 'N', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '历史');

