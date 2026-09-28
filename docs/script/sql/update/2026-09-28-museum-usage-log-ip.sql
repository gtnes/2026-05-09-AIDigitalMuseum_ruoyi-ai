-- ============================================================
-- AI博物馆调用记录：新增访问者IP与归属地（2026-09-28）
-- 目的：记录C端调用来源，支撑按IP排查刷量与频控
-- 说明：历史数据 client_ip/location 为空，属正常（新记录起生效）
-- 执行后需重启后端服务（实体/查询/导出已同步更新）
-- ============================================================

ALTER TABLE ai_museum_usage_log
    ADD COLUMN client_ip VARCHAR(64) NULL DEFAULT NULL COMMENT '访问者IP（记账时固化，经X-Forwarded-For解析）' AFTER cost,
    ADD COLUMN location VARCHAR(100) NULL DEFAULT NULL COMMENT 'IP归属地（ip2region离线解析固化，内网显示"内网IP"）' AFTER client_ip,
    ADD INDEX idx_client_ip (client_ip);
