package org.ruoyi.mapper.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    /**
     * 分页查询通用AI用量流水明细（含该IP当日/累计调用次数与费用统计）
     */
    @Select("""
        <script>
        SELECT u.id, u.category, u.biz_type, u.app_id, u.voice_id, u.app_name,
               u.chars, u.tokens_in, u.tokens_out, u.cost,
               u.client_ip, u.location, u.oper_id, u.oper_name,
               (SELECT COUNT(*) FROM ai_usage_log t WHERE t.client_ip = u.client_ip AND t.create_time >= CURDATE()) AS today_calls,
               (SELECT IFNULL(SUM(t.cost), 0) FROM ai_usage_log t WHERE t.client_ip = u.client_ip AND t.create_time >= CURDATE()) AS today_cost,
               (SELECT COUNT(*) FROM ai_usage_log t WHERE t.client_ip = u.client_ip) AS total_calls,
               (SELECT IFNULL(SUM(t.cost), 0) FROM ai_usage_log t WHERE t.client_ip = u.client_ip) AS total_cost,
               u.create_time
        FROM ai_usage_log u
        <where>
            <if test="bo.category != null and bo.category != ''">AND u.category = #{bo.category}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.appName != null and bo.appName != ''">AND u.app_name LIKE CONCAT('%', #{bo.appName}, '%')</if>
            <if test="bo.clientIp != null and bo.clientIp != ''">AND u.client_ip LIKE CONCAT('%', #{bo.clientIp}, '%')</if>
            <if test="bo.params.beginTime != null">AND u.create_time &gt;= #{bo.params.beginTime}</if>
            <if test="bo.params.endTime != null">AND u.create_time &lt; DATE_ADD(#{bo.params.endTime}, INTERVAL 1 DAY)</if>
        </where>
        ORDER BY u.id DESC
        </script>
        """)
    IPage<AiUsageLogVo> selectUsageLogPage(IPage<AiUsageLogVo> page, @Param("bo") AiUsageLogBo bo);

    /**
     * 查询通用AI用量流水明细列表（导出用，条件同上）
     */
    @Select("""
        <script>
        SELECT u.id, u.category, u.biz_type, u.app_id, u.voice_id, u.app_name,
               u.chars, u.tokens_in, u.tokens_out, u.cost,
               u.client_ip, u.location, u.oper_id, u.oper_name,
               (SELECT COUNT(*) FROM ai_usage_log t WHERE t.client_ip = u.client_ip AND t.create_time >= CURDATE()) AS today_calls,
               (SELECT IFNULL(SUM(t.cost), 0) FROM ai_usage_log t WHERE t.client_ip = u.client_ip AND t.create_time >= CURDATE()) AS today_cost,
               (SELECT COUNT(*) FROM ai_usage_log t WHERE t.client_ip = u.client_ip) AS total_calls,
               (SELECT IFNULL(SUM(t.cost), 0) FROM ai_usage_log t WHERE t.client_ip = u.client_ip) AS total_cost,
               u.create_time
        FROM ai_usage_log u
        <where>
            <if test="bo.category != null and bo.category != ''">AND u.category = #{bo.category}</if>
            <if test="bo.bizType != null and bo.bizType != ''">AND u.biz_type = #{bo.bizType}</if>
            <if test="bo.appName != null and bo.appName != ''">AND u.app_name LIKE CONCAT('%', #{bo.appName}, '%')</if>
            <if test="bo.clientIp != null and bo.clientIp != ''">AND u.client_ip LIKE CONCAT('%', #{bo.clientIp}, '%')</if>
            <if test="bo.params.beginTime != null">AND u.create_time &gt;= #{bo.params.beginTime}</if>
            <if test="bo.params.endTime != null">AND u.create_time &lt; DATE_ADD(#{bo.params.endTime}, INTERVAL 1 DAY)</if>
        </where>
        ORDER BY u.id DESC
        </script>
        """)
    List<AiUsageLogVo> selectUsageLogList(@Param("bo") AiUsageLogBo bo);

}
