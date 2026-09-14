package org.ruoyi.service.voice;

import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.voice.PlatformVoiceBo;
import org.ruoyi.domain.vo.voice.PlatformVoiceVo;

import java.util.Collection;
import java.util.List;

/**
 * 平台音色字典Service接口
 *
 * @author ruoyi
 * @date 2026-09-14
 */
public interface IPlatformVoiceService {

    /**
     * 查询平台音色
     */
    PlatformVoiceVo queryById(Long id);

    /**
     * 分页查询平台音色列表
     */
    TableDataInfo<PlatformVoiceVo> queryPageList(PlatformVoiceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的平台音色列表
     */
    List<PlatformVoiceVo> queryList(PlatformVoiceBo bo);

    /**
     * 新增平台音色
     */
    Boolean insertByBo(PlatformVoiceBo bo);

    /**
     * 修改平台音色
     */
    Boolean updateByBo(PlatformVoiceBo bo);

    /**
     * 校验并批量删除平台音色
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
