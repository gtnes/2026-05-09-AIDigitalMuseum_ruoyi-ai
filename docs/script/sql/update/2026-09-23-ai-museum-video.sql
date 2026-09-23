-- ============================================================
-- AI博物馆 AI视频配置 补丁脚本：
--   ai_museum 新增 AI视频开关 + 视频分类 + AI视频讲解员 三列。
--   配合管理端"AI视频开关"表单项（开启后可选分类与讲解员），
--   讲解员取值为本博物馆智能体配置中的 chat_app.id。
--   ALTER 语句不可重复执行（重复执行会报列已存在）。
-- ============================================================

ALTER TABLE `ai_museum`
    ADD COLUMN `video_enable` tinyint NOT NULL DEFAULT 0 COMMENT '是否开启AI视频（0关闭 1开启）' AFTER `vr_url`,
    ADD COLUMN `video_category_id` bigint NULL DEFAULT NULL COMMENT 'AI视频分类id（ai_video_category.id）' AFTER `video_enable`,
    ADD COLUMN `video_chatapp_id` bigint NULL DEFAULT NULL COMMENT 'AI视频讲解员（chat_app.id，从本博物馆智能体配置中选择）' AFTER `video_category_id`;
