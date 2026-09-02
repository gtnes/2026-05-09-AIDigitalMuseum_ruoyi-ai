package org.ruoyi.service.chat.impl;

import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ruoyi.domain.bo.chat.ChatAppBo;
import org.ruoyi.domain.vo.chat.ChatAppSimpleVo;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.mapper.chat.ChatAppMapper;
import org.ruoyi.service.chat.IChatAppService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 应用管理Service业务层处理
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatAppServiceImpl implements IChatAppService {

    private final ChatAppMapper baseMapper;

    /**
     * 查询应用管理
     *
     * @param id 主键
     * @return 应用管理
     */
    @Override
    public ChatAppVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询应用管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 应用管理分页列表
     */
    @Override
    public TableDataInfo<ChatAppVo> queryPageList(ChatAppBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChatApp> lqw = buildQueryWrapper(bo);
        Page<ChatAppVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的应用管理列表
     *
     * @param bo 查询条件
     * @return 应用管理列表
     */
    @Override
    public List<ChatAppVo> queryList(ChatAppBo bo) {
        LambdaQueryWrapper<ChatApp> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询启用的应用列表（精简信息，不包含敏感字段）
     *
     * @return 应用列表
     */
    @Override
    public List<ChatAppSimpleVo> queryEnableList() {
        LambdaQueryWrapper<ChatApp> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChatApp::getStatus, "0");
        lqw.orderByAsc(ChatApp::getId);
        return baseMapper.selectVoList(lqw, ChatAppSimpleVo.class);
    }

    private LambdaQueryWrapper<ChatApp> buildQueryWrapper(ChatAppBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ChatApp> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ChatApp::getId);
        lqw.like(StringUtils.isNotBlank(bo.getAppName()), ChatApp::getAppName, bo.getAppName());
        lqw.eq(StringUtils.isNotBlank(bo.getAppType()), ChatApp::getAppType, bo.getAppType());
        lqw.eq(StringUtils.isNotBlank(bo.getProviderCode()), ChatApp::getProviderCode, bo.getProviderCode());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChatApp::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增应用管理
     *
     * @param bo 应用管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ChatAppBo bo) {
        ChatApp add = MapstructUtils.convert(bo, ChatApp.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改应用管理
     *
     * @param bo 应用管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ChatAppBo bo) {
        ChatApp update = MapstructUtils.convert(bo, ChatApp.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ChatApp entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除应用管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
