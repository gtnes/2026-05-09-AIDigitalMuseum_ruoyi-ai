-- ============================================================
-- 应用管理（chat_app）新增：智能体ID字段
-- agent_id 百炼知识问答v2接口的agent_id（aid-xxx格式）
--   - 非空：走新版知识问答接口
--     POST https://{workspaceId}.cn-beijing.maas.aliyuncs.com/api/v2/apps/knowledge/chat
--     （agent_id 放请求体 parameters.agent_options.agent_id，请求地址填完整 v2 URL）
--   - 为空：走旧版应用调用v1接口
--     POST https://dashscope.aliyuncs.com/api/v1/apps/{APP_ID}/completion
-- 注意：仅可执行一次（ALTER 不可重复执行）。
-- ============================================================

ALTER TABLE `chat_app`
    ADD COLUMN `agent_id` varchar(64) NULL DEFAULT NULL COMMENT '智能体ID（百炼知识问答v2接口agent_id，aid-xxx格式；旧v1应用调用为空）' AFTER `api_key`;
