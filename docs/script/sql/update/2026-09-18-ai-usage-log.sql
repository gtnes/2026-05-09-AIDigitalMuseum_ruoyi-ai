-- ============================================================
-- 通用AI用量流水（系统类别）初始化脚本
-- 记录登录通用接口（chat/send、voice/tts，apps-chat测试页等系统内部调用）的用量：
--   对话按token计费（chat_app 单价）、语音按字符计费（voice_profile 单价）
-- 应用名称/操作人按调用当时快照固化，费用按当时单价固化
-- 执行后需重启后端服务
-- ============================================================

-- 1. 通用AI用量流水表（已加入租户排除清单，见 application.yml tenant.excludes）
CREATE TABLE ai_usage_log (
    id          BIGINT        NOT NULL COMMENT '主键',
    category    VARCHAR(20)   NOT NULL DEFAULT 'system' COMMENT '类别（system系统内部/测试调用，预留扩展）',
    biz_type    VARCHAR(10)   NOT NULL COMMENT '业务类型（chat对话 tts语音合成）',
    app_id      BIGINT        NULL DEFAULT NULL COMMENT '智能体ID（chat_app.id，对话类记录）',
    voice_id    BIGINT        NULL DEFAULT NULL COMMENT '音色档案ID（voice_profile.id，语音类记录）',
    app_name    VARCHAR(100)  NULL DEFAULT NULL COMMENT '应用名称快照（chat=智能体名称，tts=音色名称）',
    chars       INT           NOT NULL DEFAULT 0 COMMENT '字符数（对话=输入内容长度，TTS=合成文本长度）',
    tokens_in   INT           NOT NULL DEFAULT 0 COMMENT '输入token数（对话类记录，来自百炼usage）',
    tokens_out  INT           NOT NULL DEFAULT 0 COMMENT '输出token数（对话类记录，来自百炼usage）',
    cost        DECIMAL(12, 6) NOT NULL DEFAULT 0 COMMENT '费用（元，按当时单价固化）',
    oper_id     BIGINT        NULL DEFAULT NULL COMMENT '操作人ID（登录用户）',
    oper_name   VARCHAR(50)   NULL DEFAULT NULL COMMENT '操作人昵称快照',
    create_time DATETIME      NOT NULL COMMENT '调用时间',
    PRIMARY KEY (id),
    KEY idx_category_time (category, create_time),
    KEY idx_biz_time (biz_type, create_time),
    KEY idx_app (app_id),
    KEY idx_voice (voice_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '通用AI用量流水表';

-- ============================================================
-- 2. 通用用量记录菜单（挂在"智能体应用"目录下，排在AI博物馆用量统计之后）
-- 注意：本节使用 INSERT IGNORE，可重复执行（幂等）
-- ============================================================
INSERT IGNORE INTO `sys_menu` VALUES
(2099010400000000201, '通用用量记录', 2089647684201230337, 4, 'usage', 'chat/usage/index', NULL, 1, 0, 'C', '0', '0', 'system:usage:list', 'ant-design:fund-outlined', 103, 1, '2026-09-18 00:00:00', NULL, NULL, '通用AI用量流水记录（登录通用接口调用）'),
(2099010400000000202, '用量查询', 2099010400000000201, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:usage:query', '#', 103, 1, '2026-09-18 00:00:00', NULL, NULL, ''),
(2099010400000000203, '用量导出', 2099010400000000201, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:usage:export', '#', 103, 1, '2026-09-18 00:00:00', NULL, NULL, ''),
-- 3. 通用用量统计菜单（聚合汇总视图，挂在"通用用量记录"之后）
(2099010400000000301, '通用用量统计', 2089647684201230337, 5, 'usagesummary', 'chat/usagesummary/index', NULL, 1, 0, 'C', '0', '0', 'system:usage:query', 'ant-design:pie-chart-outlined', 103, 1, '2026-09-18 00:00:00', NULL, NULL, '通用AI用量汇总统计（按类别/应用/业务类型聚合）');
