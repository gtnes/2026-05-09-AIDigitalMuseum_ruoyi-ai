-- ============================================================
-- AI博物馆应用：建表 + 菜单权限
-- 表 ai_museum：博物馆实例（机构名称、标题、Logo、banner、
--   服务起止时间、过期提示、VR配置、chatapps智能体配置列表等）
-- chatapps 为 JSON 字段，直接存储智能体配置列表
--   （元素 id 为 chat_app.id，appName 来自chatapp不落库，查询时补充）
-- 注意：status 与 vrEnable 采用业务约定 1正常/开启、0停用/关闭
--       （与 sys_normal_disable 字典 0正常 1停用 不同）
-- 本脚本幂等，可重复执行（INSERT IGNORE 天然幂等）。
-- ============================================================

-- ----------------------------
-- 表：AI博物馆实例表
-- ----------------------------
DROP TABLE IF EXISTS `ai_museum`;
CREATE TABLE `ai_museum` (
    `id` bigint NOT NULL COMMENT '主键',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '实例状态（1正常 0停用）',
    `org_title` varchar(100) NULL DEFAULT '' COMMENT '原始机构名称',
    `title` varchar(100) NOT NULL COMMENT '展示标题',
    `logo_url` varchar(500) NULL DEFAULT '' COMMENT 'Logo地址（ossId）',
    `banner_url` varchar(500) NULL DEFAULT '' COMMENT 'banner背景图地址（ossId）',
    `start_time` bigint NULL DEFAULT NULL COMMENT '服务开始时间戳（秒）',
    `end_time` bigint NULL DEFAULT NULL COMMENT '服务到期时间戳（秒）',
    `expire_tips` varchar(500) NULL DEFAULT '' COMMENT '过期提示文案',
    `custom_name` varchar(100) NULL DEFAULT '' COMMENT '自定义名称',
    `vr_enable` tinyint NOT NULL DEFAULT 0 COMMENT '是否开启VR（0关闭 1开启）',
    `vr_url` varchar(500) NULL DEFAULT '' COMMENT 'VR地址',
    `chatapps` json NULL COMMENT '智能体配置列表（JSON数组，元素id为chat_app.id）',
    `operation_list` json NULL COMMENT '权限操作列表（保留字段，暂未启用）',
    `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
    `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户Id',
    `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_ai_museum_tenant_id` (`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI博物馆实例表' ROW_FORMAT = DYNAMIC;

-- ============================================================
-- 菜单：挂在 智能体应用(2089647684201230337) 下，排在 应用管理 之后
-- 组件对应管理端 chat/aimuseum/index
-- ============================================================
INSERT IGNORE INTO `sys_menu` VALUES
(2099010400000000001, 'AI博物馆', 2089647684201230337, 2, 'aimuseum', 'chat/aimuseum/index', NULL, 1, 0, 'C', '0', '0', 'system:aimuseum:list', 'ant-design:bank-outlined', 103, 1, '2026-09-04 00:00:00', NULL, NULL, 'AI博物馆菜单'),
(2099010400000000002, 'AI博物馆查询', 2099010400000000001, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:aimuseum:query', '#', 103, 1, '2026-09-04 00:00:00', NULL, NULL, ''),
(2099010400000000003, 'AI博物馆新增', 2099010400000000001, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:aimuseum:add', '#', 103, 1, '2026-09-04 00:00:00', NULL, NULL, ''),
(2099010400000000004, 'AI博物馆修改', 2099010400000000001, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:aimuseum:edit', '#', 103, 1, '2026-09-04 00:00:00', NULL, NULL, ''),
(2099010400000000005, 'AI博物馆删除', 2099010400000000001, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:aimuseum:remove', '#', 103, 1, '2026-09-04 00:00:00', NULL, NULL, ''),
(2099010400000000006, 'AI博物馆导出', 2099010400000000001, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:aimuseum:export', '#', 103, 1, '2026-09-04 00:00:00', NULL, NULL, '');
