package org.ruoyi.mapper.chat;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.entity.chat.AiMuseumUsageLog;
import org.ruoyi.domain.vo.chat.MuseumUsageSummaryVo;

import java.util.List;

/**
 * AI博物馆用量流水Mapper接口
 *
 * @author gtnes
 * @date 2026-09-18
 */
public interface AiMuseumUsageLogMapper extends BaseMapperPlus<AiMuseumUsageLog, AiMuseumUsageLog> {

    /**
     * 按博物馆+业务类型聚合用量与费用
     * ai_museum_usage_log已加入租户排除；ai_museum保持租户过滤（管理端按租户可见）
     */
    @Select("""
        <script>
        SELECT u.museum_id, m.title AS museum_title, u.biz_type,
               COUNT(*) AS calls,
               IFNULL(SUM(u.chars), 0) AS chars,
               IFNULL(SUM(u.tokens_in), 0) AS tokens_in,
               IFNULL(SUM(u.tokens_out), 0) AS tokens_out,
               IFNULL(SUM(u.cost), 0) AS cost
        FROM ai_museum_usage_log u
        LEFT JOIN ai_museum m ON u.museum_id = m.id
        <where>
            <if test="bo.museumId != null">AND u.museum_id = #{bo.museumId}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.beginTime != null">AND u.create_time &gt;= #{bo.beginTime}</if>
            <if test="bo.endTime != null">AND u.create_time &lt; DATE_ADD(#{bo.endTime}, INTERVAL 1 DAY)</if>
        </where>
        GROUP BY u.museum_id, m.title, u.biz_type
        ORDER BY cost DESC
        </script>
        """)
    List<MuseumUsageSummaryVo> selectUsageSummary(@Param("bo") MuseumUsageQueryBo bo);

}
