package org.ruoyi.service.chat;

import org.ruoyi.domain.vo.chat.ChatAppSimpleVo;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.domain.bo.chat.ChatAppBo;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 应用管理Service接口
 *
 * @author gtnes
 * @date 2026-08-19
 */
public interface IChatAppService {

    /**
     * 查询应用管理
     *
     * @param id 主键
     * @return 应用管理
     */
    ChatAppVo queryById(Long id);

    /**
     * 分页查询应用管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 应用管理分页列表
     */
    TableDataInfo<ChatAppVo> queryPageList(ChatAppBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的应用管理列表
     *
     * @param bo 查询条件
     * @return 应用管理列表
     */
    List<ChatAppVo> queryList(ChatAppBo bo);

    /**
     * 查询启用的应用列表（精简信息，不包含敏感字段）
     *
     * @return 应用列表
     */
    List<ChatAppSimpleVo> queryEnableList();

    /**
     * 按ID查询启用的应用（精简信息，不包含敏感字段）
     *
     * @param id 主键
     * @return 应用信息（未启用或不存在时返回null）
     */
    ChatAppSimpleVo queryEnableById(Long id);

    /**
     * 新增应用管理
     *
     * @param bo 应用管理
     * @return 是否新增成功
     */
    Boolean insertByBo(ChatAppBo bo);

    /**
     * 修改应用管理
     *
     * @param bo 应用管理
     * @return 是否修改成功
     */
    Boolean updateByBo(ChatAppBo bo);

    /**
     * 校验并批量删除应用管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
