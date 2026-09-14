package org.ruoyi.service.voice;

import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.voice.VoiceProfileBo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;

import java.util.Collection;
import java.util.List;

/**
 * AI语音音色档案Service接口
 *
 * @author ruoyi
 * @date 2026-09-14
 */
public interface IVoiceProfileService {

    /**
     * 查询音色档案
     */
    VoiceProfileVo queryById(Long id);

    /**
     * 分页查询音色档案列表
     */
    TableDataInfo<VoiceProfileVo> queryPageList(VoiceProfileBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音色档案列表
     */
    List<VoiceProfileVo> queryList(VoiceProfileBo bo);

    /**
     * 新增音色档案
     */
    Boolean insertByBo(VoiceProfileBo bo);

    /**
     * 修改音色档案
     */
    Boolean updateByBo(VoiceProfileBo bo);

    /**
     * 校验并批量删除音色档案
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
