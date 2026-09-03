package org.ruoyi.mapper.chat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ruoyi.common.mybatis.annotation.DataColumn;
import org.ruoyi.common.mybatis.annotation.DataPermission;
import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 应用管理Mapper接口
 *
 * @author gtnes
 * @date 2026-08-19
 */
public interface ChatAppMapper extends BaseMapperPlus<ChatApp, ChatAppVo> {

    /**
     * 分页查询应用管理列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<ChatAppVo> selectPageAppList(Page<ChatApp> page, Wrapper<ChatApp> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 查询应用管理列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default List<ChatAppVo> selectAppList(Wrapper<ChatApp> queryWrapper) {
        return this.selectVoList(queryWrapper);
    }
}
