-- ============================================================
-- AI视频 ossId 存储说明 补丁脚本（纯注释修正，无结构/数据变更）：
--   仅当已执行过旧版 2026-09-22-ai-video.sql（列注释为"封面图片URL/
--   视频文件URL"）的库需要执行本脚本，用于把列注释与最新主脚本对齐；
--   未执行过旧版主脚本的库无需本脚本（新建表注释已正确）。
--   说明：cover_url/video_url 实际存储 sys_oss.ossId 编号（与
--   chat_app.app_show 同一模式），并非落库签名URL（签名120秒即失效）。
--   本脚本可重复执行（重复执行无副作用）。
-- ============================================================

-- 列注释对齐（MODIFY 需完整重述列定义，此处与主脚本保持一致，仅注释变化）
ALTER TABLE `ai_video`
    MODIFY COLUMN `cover_url` varchar(500) NULL DEFAULT NULL COMMENT '封面图片（sys_oss.ossId）',
    MODIFY COLUMN `video_url` varchar(500) NOT NULL COMMENT '视频文件（sys_oss.ossId）',
    MODIFY COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT '所属分类ID(ai_video_category.id，仅内部筛选用，不对外)';
