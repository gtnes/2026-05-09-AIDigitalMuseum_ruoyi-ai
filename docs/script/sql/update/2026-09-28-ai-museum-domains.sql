-- ============================================================
-- AI博物馆 域名配置 补丁脚本：
--   ai_museum 新增 domains 列（JSON数组），可配置多个域名，
--   默认为空。元素结构：{"domain":"域名","envType":"dev本地开发/prod生产环境"}
--   配合管理端"智能体配置"下方的"域名配置"表单区域。
--   ALTER 语句不可重复执行（重复执行会报列已存在）。
-- ============================================================

ALTER TABLE `ai_museum`
    ADD COLUMN `domains` json NULL DEFAULT NULL COMMENT '域名配置列表（JSON数组，元素含域名domain与环境类型envType，默认为空）' AFTER `chatapps`;

-- ============================================================
-- 数据修复：旧格式字符串数组（如 ["http://localhost:5173"]）
--   转为对象格式 [{"domain":"http://localhost:5173","envType":"dev"}]。
--   envType 缺省为 dev（本地开发）。仅处理元素为字符串的行，幂等可重复执行。
-- ============================================================
UPDATE `ai_museum`
SET `domains` = (
    SELECT JSON_ARRAYAGG(JSON_OBJECT('domain', t.elem, 'envType', 'dev'))
    FROM JSON_TABLE(`domains`, '$[*]' COLUMNS (elem VARCHAR(500) PATH '$')) AS t
)
WHERE `domains` IS NOT NULL
  AND JSON_TYPE(`domains`) = 'ARRAY'
  AND JSON_LENGTH(`domains`) > 0
  AND JSON_TYPE(JSON_EXTRACT(`domains`, '$[0]')) = 'STRING';
