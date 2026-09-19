package org.ruoyi.mapper.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.ruoyi.domain.bo.chat.MuseumUsageLogBo;
import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.entity.chat.AiMuseumUsageLog;
import org.ruoyi.domain.vo.chat.MuseumUsageLogVo;
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

    /**
     * 分页查询博物馆用量流水明细（带博物馆/智能体/音色名称）
     * ai_museum_usage_log已加入租户排除；关联表保持租户过滤（管理端按租户可见）
     */
    @Select("""
        <script>
        SELECT u.id, u.museum_id, m.title AS museum_title, u.biz_type,
               u.app_id, a.app_name, u.voice_id, v.voice_name,
               u.chars, u.tokens_in, u.tokens_out, u.cost, u.create_time
        FROM ai_museum_usage_log u
        LEFT JOIN ai_museum m ON u.museum_id = m.id
        LEFT JOIN chat_app a ON u.app_id = a.id
        LEFT JOIN voice_profile v ON u.voice_id = v.id
        <where>
            <if test="bo.museumId != null">AND u.museum_id = #{bo.museumId}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.params.beginTime != null">AND u.create_time &gt;= #{bo.params.beginTime}</if>
            <if test="bo.params.endTime != null">AND u.create_time &lt;= #{bo.params.endTime}</if>
        </where>
        ORDER BY u.id DESC
        </script>
        """)
    IPage<MuseumUsageLogVo> selectUsageLogPage(IPage<MuseumUsageLogVo> page, @Param("bo") MuseumUsageLogBo bo);

    /**
     * 查询博物馆用量流水明细列表（导出用，条件同上）
     */
    @Select("""
        <script>
        SELECT u.id, u.museum_id, m.title AS museum_title, u.biz_type,
               u.app_id, a.app_name, u.voice_id, v.voice_name,
               u.chars, u.tokens_in, u.tokens_out, u.cost, u.create_time
        FROM ai_museum_usage_log u
        LEFT JOIN ai_museum m ON u.museum_id = m.id
        LEFT JOIN chat_app a ON u.app_id = a.id
        LEFT JOIN voice_profile v ON u.voice_id = v.id
        <where>
            <if test="bo.museumId != null">AND u.museum_id = #{bo.museumId}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.params.beginTime != null">AND u.create_time &gt;= #{bo.params.beginTime}</if>
            <if test="bo.params.endTime != null">AND u.create_time &lt;= #{bo.params.endTime}</if>
        </where>
        ORDER BY u.id DESC
        </script>
        """)
    List<MuseumUsageLogVo> selectUsageLogList(@Param("bo") MuseumUsageLogBo bo);

}
