-- ============================================================
-- 智能体语音播报（TTS）功能：
--   voice_platform 平台音色字典表（阿里云 CosyVoice / OpenAI 预置音色，支持自定义扩展）
--   voice_profile  业务音色档案表（如"西西"，绑定平台+平台音色+微调参数+平台凭证）
--   菜单：AI语音管理、平台音色库（挂"智能体应用"目录 2089647684201230337 下，
--         紧跟"智能体应用管理"菜单之后）
-- 本脚本幂等，可重复执行（CREATE IF NOT EXISTS + INSERT IGNORE）。
-- menu_id 使用 2099010500000000xxx 段，音色字典数据 id 使用 2099010500000001xxx 段，
-- 与已有 2026-07-24（...010000...）、2026-09-04（...040000...）脚本不冲突。
-- ============================================================

-- ============================================================
-- 1. 平台音色字典表
-- ============================================================
CREATE TABLE IF NOT EXISTS `voice_platform` (
    `id` bigint NOT NULL COMMENT '主键',
    `platform` varchar(32) NOT NULL COMMENT '平台标识(aliyun/openai)',
    `voice_code` varchar(64) NOT NULL COMMENT '平台音色编码(如 longwan/alloy)',
    `voice_name` varchar(64) NOT NULL COMMENT '音色显示名(如 龙婉)',
    `gender` char(1) NULL DEFAULT NULL COMMENT '性别(0男 1女 2未知)',
    `description` varchar(255) NULL DEFAULT NULL COMMENT '音色描述',
    `status` char(1) NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户Id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_platform_voice_code` (`platform`, `voice_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台音色字典' ROW_FORMAT = DYNAMIC;

-- ============================================================
-- 2. 业务音色档案表
-- ============================================================
CREATE TABLE IF NOT EXISTS `voice_profile` (
    `id` bigint NOT NULL COMMENT '主键',
    `voice_name` varchar(64) NOT NULL COMMENT '音色名称(如 西西)',
    `avatar` varchar(500) NULL DEFAULT NULL COMMENT '音色头像URL',
    `platform` varchar(32) NOT NULL COMMENT '平台标识(aliyun/openai)',
    `platform_voice_id` bigint NOT NULL COMMENT '平台音色ID(voice_platform.id)',
    `model_id` bigint NULL DEFAULT NULL COMMENT '关联模型管理ID(chat_model.id，提供平台apiHost/apiKey)',
    `speed` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '语速(0.5-2.0，默认1.0)',
    `pitch` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '音调(0.5-2.0，默认1.0)',
    `volume` decimal(5, 2) NULL DEFAULT 50.00 COMMENT '音量(0-100，默认50)',
    `sample_text` varchar(500) NULL DEFAULT NULL COMMENT '试听文本',
    `status` char(1) NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `sort` int NULL DEFAULT 0 COMMENT '显示顺序',
    `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户Id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_voice_profile_platform` (`platform`) USING BTREE,
    INDEX `idx_voice_profile_platform_voice` (`platform_voice_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI语音音色档案' ROW_FORMAT = DYNAMIC;

-- ============================================================
-- 3. 平台音色初始数据
--    阿里云 CosyVoice（cosyvoice-v3-flash）官方音色 16 个
--    OpenAI（tts-1/gpt-4o-mini-tts）音色 6 个
-- ============================================================
INSERT IGNORE INTO `voice_platform` VALUES
-- 阿里云 CosyVoice（cosyvoice-v3-flash 官方音色列表，端点 /api/v1/services/audio/tts/SpeechSynthesizer）
(2099010500000001001, 'aliyun', 'longanyang', '龙安洋', '0', '阳光大男孩，社交陪伴', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001002, 'aliyun', 'longanhuan_v3', '龙安欢（V3）', '1', '欢脱元气女，多方言', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001003, 'aliyun', 'longanhuan', '龙安欢', '1', '欢脱元气女，普通话', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001004, 'aliyun', 'longhuhu_v3', '龙呼呼', '1', '天真烂漫女童', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001005, 'aliyun', 'longpaopao_v3', '龙泡泡', '2', '飞天泡泡音，童声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001006, 'aliyun', 'longjielidou_v3', '龙杰力豆', '0', '阳光顽皮男童', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001007, 'aliyun', 'longxian_v3', '龙仙', '1', '豪放可爱女', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001008, 'aliyun', 'longling_v3', '龙铃', '1', '稚气呆板女童', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001009, 'aliyun', 'longshanshan_v3', '龙闪闪', '2', '戏剧化童声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001010, 'aliyun', 'longniuniu_v3', '龙牛牛', '0', '阳光男童声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001011, 'aliyun', 'longjiaxin_v3', '龙嘉欣', '1', '优雅粤语女', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001012, 'aliyun', 'longjiayi_v3', '龙嘉怡', '1', '知性粤语女', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001013, 'aliyun', 'longanyue_v3', '龙安粤', '0', '欢脱粤语男', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001014, 'aliyun', 'longlaotie_v3', '龙老铁', '0', '东北直率男', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001015, 'aliyun', 'longshange_v3', '龙陕哥', '0', '原味陕北男', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
(2099010500000001016, 'aliyun', 'longanmin_v3', '龙安闽', '1', '清纯萝莉女', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'cosyvoice-v3-flash', 0),
-- OpenAI
(2099010500000001101, 'openai', 'alloy', 'Alloy', '2', '中性平衡', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0),
(2099010500000001102, 'openai', 'echo', 'Echo', '0', '沉稳男声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0),
(2099010500000001103, 'openai', 'fable', 'Fable', '0', '叙事风格', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0),
(2099010500000001104, 'openai', 'onyx', 'Onyx', '0', '低沉浑厚', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0),
(2099010500000001105, 'openai', 'nova', 'Nova', '1', '活力女声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0),
(2099010500000001106, 'openai', 'shimmer', 'Shimmer', '1', '清亮女声', '0', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'OpenAI TTS', 0);

-- ============================================================
-- 3.1 阿里云TTS迁移（幂等，可重复执行）
--     旧版按 cosyvoice-v2 旧端点(text2speech)初始化，现切换为 cosyvoice-v3-flash
--     新端点(SpeechSynthesizer)；模型名更新、旧音色清理、档案关联修复。
-- ============================================================
UPDATE `chat_model` SET `model_name` = 'cosyvoice-v3-flash'
 WHERE `id` = 2099433709924519938 AND `model_name` = 'cosyvoice-v2';
-- 清理已废弃的v2系音色（voice_code 不带 _v3 后缀的阿里云音色）
DELETE FROM `voice_platform`
 WHERE `platform` = 'aliyun' AND `voice_code` NOT IN
   ('longanyang', 'longanhuan_v3', 'longanhuan', 'longhuhu_v3', 'longpaopao_v3', 'longjielidou_v3',
    'longxian_v3', 'longling_v3', 'longshanshan_v3', 'longniuniu_v3', 'longjiaxin_v3', 'longjiayi_v3',
    'longanyue_v3', 'longlaotie_v3', 'longshange_v3', 'longanmin_v3');
-- 旧档案关联悬空时指向新音色（可按需调整）
UPDATE `voice_profile` SET `platform_voice_id` = 2099010500000001016
 WHERE `platform` = 'aliyun' AND `platform_voice_id` NOT IN (SELECT `id` FROM `voice_platform` WHERE `platform` = 'aliyun');

-- ============================================================
-- 4. 菜单：AI语音管理 + 平台音色库
--    父菜单=智能体应用目录(2089647684201230337)，紧跟"智能体应用管理"菜单(2089898903779946498)之后；
--    AI博物馆(2099010400000000001)排序后移至4，为语音菜单让位。
--    末尾附迁移UPDATE：已按旧版（挂对话管理2000209300188356609）执行过本脚本的环境，
--    重复执行即可将菜单移动到新位置（幂等）。
-- ============================================================
INSERT IGNORE INTO `sys_menu` VALUES
-- AI语音管理
(2099010500000000001, 'AI语音管理', 2089647684201230337, 2, 'voice-profile', 'voice/profile/index', NULL, 1, 0, 'C', '0', '0', 'voice:profile:list', 'ant-design:sound-outlined', 103, 1, '2026-09-14 00:00:00', NULL, NULL, 'AI语音音色档案菜单'),
(2099010500000000002, 'AI语音管理查询', 2099010500000000001, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:profile:query', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000003, 'AI语音管理新增', 2099010500000000001, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:profile:add', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000004, 'AI语音管理修改', 2099010500000000001, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:profile:edit', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000005, 'AI语音管理删除', 2099010500000000001, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:profile:remove', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000006, 'AI语音管理导出', 2099010500000000001, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:profile:export', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
-- 平台音色库
(2099010500000000011, '平台音色库', 2089647684201230337, 3, 'platform-voice', 'voice/platform/index', NULL, 1, 0, 'C', '0', '0', 'voice:platform:list', 'ant-design:audio-outlined', 103, 1, '2026-09-14 00:00:00', NULL, NULL, '平台音色字典菜单'),
(2099010500000000012, '平台音色库查询', 2099010500000000011, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:platform:query', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000013, '平台音色库新增', 2099010500000000011, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:platform:add', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000014, '平台音色库修改', 2099010500000000011, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:platform:edit', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000015, '平台音色库删除', 2099010500000000011, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:platform:remove', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, ''),
(2099010500000000016, '平台音色库导出', 2099010500000000011, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'voice:platform:export', '#', 103, 1, '2026-09-14 00:00:00', NULL, NULL, '');

-- ============================================================
-- 5. 菜单位置迁移（幂等，可重复执行）
--    旧版脚本将语音菜单挂在"对话管理"目录下，此处移动到"云山AI"目录，
--    紧跟"智能体应用管理"菜单之后；AI博物馆排序后移至4。
-- ============================================================
UPDATE `sys_menu` SET `parent_id` = 2089647684201230337, `order_num` = 2 WHERE `menu_id` = 2099010500000000001;
UPDATE `sys_menu` SET `parent_id` = 2089647684201230337, `order_num` = 3 WHERE `menu_id` = 2099010500000000011;
UPDATE `sys_menu` SET `order_num` = 4 WHERE `menu_id` = 2099010400000000001 AND `parent_id` = 2089647684201230337;
