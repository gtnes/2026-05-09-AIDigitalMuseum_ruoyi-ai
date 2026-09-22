package org.ruoyi.service.video;

import org.ruoyi.domain.bo.video.AiVideoCategoryBo;
import org.ruoyi.domain.vo.video.AiVideoCategoryVo;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * AI视频分类Service接口
 *
 * @author gtnes
 * @date 2026-09-22
 */
public interface IAiVideoCategoryService {

    /**
     * 查询AI视频分类
     *
     * @param id 主键
     * @return AI视频分类
     */
    AiVideoCategoryVo queryById(Long id);

    /**
     * 分页查询AI视频分类列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI视频分类分页列表
     */
    TableDataInfo<AiVideoCategoryVo> queryPageList(AiVideoCategoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI视频分类列表
     *
     * @param bo 查询条件
     * @return AI视频分类列表
     */
    List<AiVideoCategoryVo> queryList(AiVideoCategoryBo bo);

    /**
     * 查询启用中的分类下拉选项（按sort排序）
     *
     * @return 分类列表
     */
    List<AiVideoCategoryVo> queryOptions();

    /**
     * 新增AI视频分类
     *
     * @param bo AI视频分类
     * @return 是否新增成功
     */
    Boolean insertByBo(AiVideoCategoryBo bo);

    /**
     * 修改AI视频分类
     *
     * @param bo AI视频分类
     * @return 是否修改成功
     */
    Boolean updateByBo(AiVideoCategoryBo bo);

    /**
     * 校验并批量删除AI视频分类信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
