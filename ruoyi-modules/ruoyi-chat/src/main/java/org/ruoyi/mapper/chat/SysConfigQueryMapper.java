package org.ruoyi.mapper.chat;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统参数直查Mapper（跨模块读取sys_config，供chat模块使用）
 * <p>
 * sys_config不在租户排除表，调用方需以TenantHelper.ignore包裹绕过租户过滤；
 * 固定读取默认租户(000000)的参数行，避免多租户同key多行。
 *
 * @author gtnes
 * @date 2026-09-28
 */
public interface SysConfigQueryMapper {

    /**
     * 按参数键名查询参数值（主键点查，无匹配返回null）
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{configKey} AND tenant_id = '000000' LIMIT 1")
    String selectValueByKey(@Param("configKey") String configKey);

}
