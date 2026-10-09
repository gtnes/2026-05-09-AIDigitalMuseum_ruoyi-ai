-- ============================================================
-- AI博物馆 模块别名 补丁脚本：
--   ai_museum 新增 AI视频模块别名 + VR模块别名 两列（非必填）。
--   配合管理端"AI视频模块别名"/"VR模块别名"表单项，
--   C端底部tabbar的"AI视频"/"虚拟空间"标签按别名显示（空=默认文案）。
--   ALTER 语句不可重复执行（重复执行会报列已存在）。
--   注：智能体状态/首页显示配置存储于 ai_museum.chatapps JSON 字段元素内，无需改表。
-- ============================================================

ALTER TABLE `ai_museum`
    ADD COLUMN `video_alias` varchar(50) NULL DEFAULT NULL COMMENT 'AI视频模块别名（空=默认"AI视频"）' AFTER `video_chatapp_id`,
    ADD COLUMN `vr_alias` varchar(50) NULL DEFAULT NULL COMMENT 'VR模块别名（空=默认"虚拟空间"）' AFTER `video_alias`;
