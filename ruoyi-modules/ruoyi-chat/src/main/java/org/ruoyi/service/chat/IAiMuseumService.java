package org.ruoyi.service.chat;

import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.chat.AiMuseumBo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;

import java.util.Collection;
import java.util.List;

/**
 * AI博物馆实例Service接口
 *
 * @author gtnes
 * @date 2026-09-04
 */
public interface IAiMuseumService {

    /**
     * 查询AI博物馆实例（含智能体配置列表）
     *
     * @param id 主键
     * @return AI博物馆实例
     */
    AiMuseumVo queryById(Long id);

    /**
     * 分页查询AI博物馆列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI博物馆分页列表
     */
    TableDataInfo<AiMuseumVo> queryPageList(AiMuseumBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI博物馆列表
     *
     * @param bo 查询条件
     * @return AI博物馆列表
     */
    List<AiMuseumVo> queryList(AiMuseumBo bo);

    /**
     * 新增AI博物馆（含智能体配置列表）
     *
     * @param bo AI博物馆
     * @return 是否新增成功
     */
    Boolean insertByBo(AiMuseumBo bo);

    /**
     * 修改AI博物馆（含智能体配置列表，JSON字段全量更新）
     *
     * @param bo AI博物馆
     * @return 是否修改成功
     */
    Boolean updateByBo(AiMuseumBo bo);

    /**
     * 校验并批量删除AI博物馆信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
