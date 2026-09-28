-- ============================================================
-- 调用记录IP统计列 + 对话/语音频控后台参数化（2026-09-28）
-- 前置：先执行 2026-09-28-museum-usage-log-ip.sql / 2026-09-28-ai-usage-log-ip.sql
-- 本脚本无表结构变更，仅插入系统参数（INSERT IGNORE幂等，可重复执行）
-- 执行后立即生效（守卫每次请求直读参数表，无需重启）
-- ============================================================

-- 博物馆C端对话防滥用参数（后台 系统管理→参数设置 可改，改完即时生效）
-- 注意：仅管对话接口 /chat/museumSend；语音合成请用下方 tts.guard.* 参数
INSERT IGNORE INTO `sys_config` VALUES
(2099010400000002101, '000000', '博物馆对话防护-总开关', 'museum.chat.guard.enabled', 'true', 'Y', 103, 1, '2026-09-28 00:00:00', NULL, NULL, '博物馆C端对话接口(/chat/museumSend)单IP频控总开关：true开启 false关闭；内网/可信环境可关闭'),
(2099010400000002102, '000000', '博物馆对话防护-单IP每分钟上限', 'museum.chat.guard.rate.limit', '20', 'Y', 103, 1, '2026-09-28 00:00:00', NULL, NULL, '单IP每分钟最大对话次数，超过拒绝；团体NAT共享出口IP可调高'),
(2099010400000002103, '000000', '博物馆对话防护-单IP每日上限', 'museum.chat.guard.daily.limit', '500', 'Y', 103, 1, '2026-09-28 00:00:00', NULL, NULL, '单IP每日最大对话次数，超过后次日自动恢复');

-- 语音合成防滥用参数（后台 系统管理→参数设置 可改，改完即时生效）
-- 注意：适用于全部语音接口——C端博物馆播报(/voice/tts/museum)与管理端试听(/voice/tts)共用同一守卫，共享同一IP计数池；签名开关与密钥仍在yml
INSERT IGNORE INTO `sys_config` VALUES
(2099010400000002104, '000000', '通用语音合成防护-单IP每分钟上限', 'tts.guard.rate.limit', '30', 'Y', 103, 1, '2026-09-28 00:00:00', NULL, NULL, '单IP每分钟最大语音合成次数，适用于全部语音接口（C端播报+管理端试听共享计数），超过拒绝'),
(2099010400000002105, '000000', '通用语音合成防护-单IP每日上限', 'tts.guard.daily.limit', '500', 'Y', 103, 1, '2026-09-28 00:00:00', NULL, NULL, '单IP每日最大语音合成次数，适用于全部语音接口（C端播报+管理端试听共享计数），超过后次日自动恢复');

-- 已初始化过旧默认值（300/100）的环境，可选执行以下UPDATE切换到500（执行后以后台参数设置为准，勿重复执行覆盖自定义值）：
-- UPDATE sys_config SET config_value = '500' WHERE config_key = 'museum.chat.guard.daily.limit' AND tenant_id = '000000';
-- UPDATE sys_config SET config_value = '500' WHERE config_key = 'tts.guard.daily.limit' AND tenant_id = '000000';

-- 语音合成防护参数改名（仅改名称与备注，不动值，幂等可重复执行）：
-- 该组参数同时约束C端博物馆播报(/voice/tts/museum)与管理端试听(/voice/tts)，名称去掉"仅博物馆"歧义
UPDATE sys_config SET config_name = '通用语音合成防护-单IP每分钟上限',
  remark = '单IP每分钟最大语音合成次数，适用于全部语音接口（C端播报+管理端试听共享计数），超过拒绝'
WHERE config_key = 'tts.guard.rate.limit' AND tenant_id = '000000';
UPDATE sys_config SET config_name = '通用语音合成防护-单IP每日上限',
  remark = '单IP每日最大语音合成次数，适用于全部语音接口（C端播报+管理端试听共享计数），超过后次日自动恢复'
WHERE config_key = 'tts.guard.daily.limit' AND tenant_id = '000000';
