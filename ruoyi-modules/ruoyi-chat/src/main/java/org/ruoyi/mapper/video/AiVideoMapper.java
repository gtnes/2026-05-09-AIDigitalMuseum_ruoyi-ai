package org.ruoyi.mapper.video;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ruoyi.common.mybatis.annotation.DataColumn;
import org.ruoyi.common.mybatis.annotation.DataPermission;
import org.ruoyi.domain.entity.video.AiVideo;
import org.ruoyi.domain.vo.video.AiVideoVo;
import org.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * AI视频Mapper接口
 *
 * @author gtnes
 * @date 2026-09-22
 */
public interface AiVideoMapper extends BaseMapperPlus<AiVideo, AiVideoVo> {

    /**
     * 分页查询AI视频列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<AiVideoVo> selectPageVideoList(Page<AiVideo> page, Wrapper<AiVideo> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 查询AI视频列表，并进行数据权限控制
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default List<AiVideoVo> selectVideoList(Wrapper<AiVideo> queryWrapper) {
        return this.selectVoList(queryWrapper);
    }
}
