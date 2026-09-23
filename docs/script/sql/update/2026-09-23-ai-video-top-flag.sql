-- ============================================================
-- AI视频 置顶字段 补丁脚本：
--   ai_video 新增 top_flag（是否置顶：0否 1是），
--   列表查询置顶优先（top_flag desc → sort asc → id asc）。
--   ALTER 语句不可重复执行（重复执行会报列已存在）。
-- ============================================================

ALTER TABLE `ai_video`
    ADD COLUMN `top_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶（0否 1是）' AFTER `sort`;
