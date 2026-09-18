-- ============================================================
-- 博物馆用量计费（精确计费模式）初始化脚本
-- 对话：按token计费（chat_app 配置输入/输出单价，元/千token）
-- 语音合成：按字符计费（voice_profile 配置单价，元/万字符）
-- 执行后需重启后端服务
-- ============================================================

-- 1. 语音音色档案：增加计费单价（元/万字符，空=不计费）
ALTER TABLE voice_profile
    ADD COLUMN price_per_10k DECIMAL(10, 4) NULL DEFAULT NULL COMMENT '计费单价（元/万字符，博物馆TTS用量计费依据，空=不计费）' AFTER sample_text;

-- 2. 智能体应用：增加token计费单价（元/千token，空=不计费）
ALTER TABLE chat_app
    ADD COLUMN price_in_per_1k DECIMAL(10, 6) NULL DEFAULT NULL COMMENT '输入token单价（元/千token，博物馆对话用量计费依据，空=不计费）' AFTER api_key,
    ADD COLUMN price_out_per_1k DECIMAL(10, 6) NULL DEFAULT NULL COMMENT '输出token单价（元/千token，博物馆对话用量计费依据，空=不计费）' AFTER price_in_per_1k;

-- 3. 博物馆用量流水表（已加入租户排除清单，见 application.yml tenant.excludes）
CREATE TABLE ai_museum_usage_log (
    id          BIGINT        NOT NULL COMMENT '主键',
    museum_id   BIGINT        NOT NULL COMMENT '博物馆ID（ai_museum.id）',
    biz_type    VARCHAR(10)   NOT NULL COMMENT '业务类型（chat对话 tts语音合成）',
    app_id      BIGINT        NULL DEFAULT NULL COMMENT '智能体ID（chat_app.id，对话类记录）',
    voice_id    BIGINT        NULL DEFAULT NULL COMMENT '音色档案ID（voice_profile.id，语音类记录）',
    chars       INT           NOT NULL DEFAULT 0 COMMENT '字符数（对话=输入内容长度，TTS=合成文本长度）',
    tokens_in   INT           NOT NULL DEFAULT 0 COMMENT '输入token数（对话类记录，来自百炼usage）',
    tokens_out  INT           NOT NULL DEFAULT 0 COMMENT '输出token数（对话类记录，来自百炼usage）',
    cost        DECIMAL(12, 6) NOT NULL DEFAULT 0 COMMENT '费用（元，按当时单价固化）',
    create_time DATETIME      NOT NULL COMMENT '调用时间',
    PRIMARY KEY (id),
    KEY idx_museum_time (museum_id, create_time),
    KEY idx_biz_time (biz_type, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI博物馆用量流水表';

-- ============================================================
-- 4. 用量统计独立菜单（挂在"智能体应用"目录下，排在AI博物馆之后）
-- 注意：本节使用 INSERT IGNORE，可重复执行（幂等）；
-- 若本脚本此前已执行过（表结构部分已生效），只需单独执行本节即可。
-- ============================================================
INSERT IGNORE INTO `sys_menu` VALUES
(2099010400000000101, '用量统计', 2089647684201230337, 3, 'museumusage', 'chat/museumusage/index', NULL, 1, 0, 'C', '0', '0', 'system:museumUsage:query', 'ant-design:bar-chart-outlined', 103, 1, '2026-09-18 00:00:00', NULL, NULL, '博物馆用量计费统计');
