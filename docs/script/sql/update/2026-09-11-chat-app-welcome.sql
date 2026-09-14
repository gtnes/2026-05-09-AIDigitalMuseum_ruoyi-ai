-- ============================================================
-- 应用管理（chat_app）新增：欢迎语 + 预设问题字段
-- welcome_msg 欢迎语（单文本）
-- preset_questions 预设问题列表（JSON数组，如 ["问题1","问题2"]）
-- 注意：仅可执行一次（ALTER 不可重复执行）。
-- ============================================================

ALTER TABLE `chat_app`
    ADD COLUMN `welcome_msg` varchar(1000) NULL DEFAULT NULL COMMENT '欢迎语' AFTER `app_show`,
    ADD COLUMN `preset_questions` json NULL COMMENT '预设问题列表（JSON数组）' AFTER `welcome_msg`;
