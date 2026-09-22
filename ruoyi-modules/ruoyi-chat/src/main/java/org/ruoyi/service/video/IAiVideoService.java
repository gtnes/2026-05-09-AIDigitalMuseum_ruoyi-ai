package org.ruoyi.service.video;

import org.ruoyi.domain.bo.video.AiVideoBo;
import org.ruoyi.domain.vo.video.AiVideoVo;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * AI视频Service接口
 *
 * @author gtnes
 * @date 2026-09-22
 */
public interface IAiVideoService {

    /**
     * 查询AI视频
     *
     * @param id 主键
     * @return AI视频
     */
    AiVideoVo queryById(Long id);

    /**
     * 分页查询AI视频列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI视频分页列表
     */
    TableDataInfo<AiVideoVo> queryPageList(AiVideoBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI视频列表
     *
     * @param bo 查询条件
     * @return AI视频列表
     */
    List<AiVideoVo> queryList(AiVideoBo bo);

    /**
     * 新增AI视频
     *
     * @param bo AI视频
     * @return 是否新增成功
     */
    Boolean insertByBo(AiVideoBo bo);

    /**
     * 修改AI视频
     *
     * @param bo AI视频
     * @return 是否修改成功
     */
    Boolean updateByBo(AiVideoBo bo);

    /**
     * 校验并批量删除AI视频信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
