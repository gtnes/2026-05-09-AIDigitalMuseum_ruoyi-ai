package org.ruoyi.mapper.chat;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.ruoyi.domain.bo.chat.AiUsageLogBo;
import org.ruoyi.domain.entity.chat.AiUsageLog;
import org.ruoyi.domain.vo.chat.AiUsageLogVo;
import org.ruoyi.domain.vo.chat.AiUsageSummaryVo;

import java.util.List;

/**
 * 通用AI用量流水Mapper接口
 *
 * @author gtnes
 * @date 2026-09-18
 */
public interface AiUsageLogMapper extends BaseMapperPlus<AiUsageLog, AiUsageLogVo> {

    /**
     * 按类别+应用+业务类型聚合用量与费用
     * ai_usage_log已加入租户排除清单（运营侧全局统计）
     */
    @Select("""
        <script>
        SELECT u.category, u.biz_type, u.app_name,
               COUNT(*) AS calls,
               IFNULL(SUM(u.chars), 0) AS chars,
               IFNULL(SUM(u.tokens_in), 0) AS tokens_in,
               IFNULL(SUM(u.tokens_out), 0) AS tokens_out,
               IFNULL(SUM(u.cost), 0) AS cost
        FROM ai_usage_log u
        <where>
            <if test="bo.category != null and bo.category != ''">AND u.category = #{bo.category}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.appName != null and bo.appName != ''">AND u.app_name LIKE CONCAT('%', #{bo.appName}, '%')</if>
            <if test="bo.params.beginTime != null">AND u.create_time &gt;= #{bo.params.beginTime}</if>
            <if test="bo.params.endTime != null">AND u.create_time &lt;= #{bo.params.endTime}</if>
        </where>
        GROUP BY u.category, u.app_name, u.biz_type
        ORDER BY cost DESC
        </script>
        """)
    List<AiUsageSummaryVo> selectUsageSummary(@Param("bo") AiUsageLogBo bo);

}
