-- ============================================================
-- AI视频展示类别 补丁脚本：
--   仅当已执行过"不含 show_category 列"的旧版
--   2026-09-22-ai-video.sql 时才需要执行本脚本；
--   尚未执行过主脚本的库无需本脚本（主脚本建表已含该列与字典）。
--   ALTER 语句不可重复执行（重复执行会报列已存在）。
-- ============================================================

-- 1. ai_video 补充展示类别列
ALTER TABLE `ai_video`
    ADD COLUMN `show_category` varchar(50) NULL DEFAULT NULL COMMENT '展示类别(对外展示：宣传片/文物/历史等，字典 ai_video_show_category)' AFTER `category_id`;

-- 2. 展示类别字典（幂等 INSERT IGNORE，与主脚本相同，重复执行安全）
INSERT IGNORE INTO `sys_dict_type` VALUES (2099010600000001001, '000000', 'AI视频展示类别', 'ai_video_show_category', 103, 1, '2026-09-22 00:00:00', NULL, NULL, 'AI视频前台展示类别（宣传片/文物/历史等）');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001011, '000000', 1, '宣传片', '宣传片', 'ai_video_show_category', '', 'primary', 'Y', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '宣传片');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001012, '000000', 2, '文物', '文物', 'ai_video_show_category', '', 'success', 'N', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '文物');
INSERT IGNORE INTO `sys_dict_data` VALUES (2099010600000001013, '000000', 3, '历史', '历史', 'ai_video_show_category', '', 'warning', 'N', 103, 1, '2026-09-22 00:00:00', NULL, NULL, '历史');
