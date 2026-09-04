package org.ruoyi.mapper.chat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ruoyi.common.mybatis.annotation.DataColumn;
import org.ruoyi.common.mybatis.annotation.DataPermission;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.ruoyi.domain.entity.chat.AiMuseum;
import org.ruoyi.domain.vo.chat.AiMuseumVo;

import java.util.List;

/**
 * AI博物馆实例Mapper接口
 *
 * @author gtnes
 * @date 2026-09-04
 */
public interface AiMuseumMapper extends BaseMapperPlus<AiMuseum, AiMuseumVo> {

    /**
     * 分页查询AI博物馆列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<AiMuseumVo> selectPageMuseumList(Page<AiMuseum> page, Wrapper<AiMuseum> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 查询AI博物馆列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default List<AiMuseumVo> selectMuseumList(Wrapper<AiMuseum> queryWrapper) {
        return this.selectVoList(queryWrapper);
    }
}
