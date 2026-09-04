package org.ruoyi.service.chat.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.exception.ServiceException;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.chat.AiMuseumBo;
import org.ruoyi.domain.entity.chat.AiMuseum;
import org.ruoyi.domain.entity.chat.AiMuseumApp;
import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.domain.vo.chat.AiMuseumAppVo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;
import org.ruoyi.mapper.chat.AiMuseumMapper;
import org.ruoyi.mapper.chat.ChatAppMapper;
import org.ruoyi.service.chat.IAiMuseumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI博物馆实例Service业务层处理
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiMuseumServiceImpl implements IAiMuseumService {

    private final AiMuseumMapper baseMapper;
    private final ChatAppMapper chatAppMapper;

    /**
     * 查询AI博物馆实例（含智能体配置列表）
     *
     * @param id 主键
     * @return AI博物馆实例
     */
    @Override
    public AiMuseumVo queryById(Long id) {
        AiMuseumVo vo = baseMapper.selectVoById(id);
        if (vo == null) {
            return null;
        }
        fillExpire(vo);
        fillAppName(vo.getChatapps());
        return vo;
    }

    /**
     * 分页查询AI博物馆列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI博物馆分页列表
     */
    @Override
    public TableDataInfo<AiMuseumVo> queryPageList(AiMuseumBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiMuseum> lqw = buildQueryWrapper(bo);
        Page<AiMuseumVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(this::fillExpire);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI博物馆列表
     *
     * @param bo 查询条件
     * @return AI博物馆列表
     */
    @Override
    public List<AiMuseumVo> queryList(AiMuseumBo bo) {
        LambdaQueryWrapper<AiMuseum> lqw = buildQueryWrapper(bo);
        List<AiMuseumVo> list = baseMapper.selectVoList(lqw);
        list.forEach(this::fillExpire);
        return list;
    }

    private LambdaQueryWrapper<AiMuseum> buildQueryWrapper(AiMuseumBo bo) {
        LambdaQueryWrapper<AiMuseum> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(AiMuseum::getId);
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), AiMuseum::getTitle, bo.getTitle());
        lqw.like(StringUtils.isNotBlank(bo.getOrgTitle()), AiMuseum::getOrgTitle, bo.getOrgTitle());
        lqw.eq(bo.getStatus() != null, AiMuseum::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增AI博物馆（含智能体配置列表）
     *
     * @param bo AI博物馆
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(AiMuseumBo bo) {
        AiMuseum add = MapstructUtils.convert(bo, AiMuseum.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改AI博物馆（含智能体配置列表）
     *
     * @param bo AI博物馆
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(AiMuseumBo bo) {
        AiMuseum update = MapstructUtils.convert(bo, AiMuseum.class);
        validEntityBeforeSave(update);
        // chatapps 为 null 时更新为空列表，避免 JSON 字段不更新
        if (update.getChatapps() == null) {
            update.setChatapps(List.of());
        }
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiMuseum entity) {
        List<AiMuseumApp> chatapps = entity.getChatapps();
        if (CollUtil.isEmpty(chatapps)) {
            return;
        }
        // 校验智能体不能重复
        long distinctCount = chatapps.stream()
            .map(AiMuseumApp::getId)
            .filter(Objects::nonNull)
            .distinct()
            .count();
        if (distinctCount != chatapps.size()) {
            throw new ServiceException("智能体不能重复选择");
        }
    }

    /**
     * 校验并批量删除AI博物馆信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 补充智能体配置中的应用名称（appName来自chat_app，不落库）
     */
    private void fillAppName(List<AiMuseumAppVo> chatapps) {
        if (CollUtil.isEmpty(chatapps)) {
            return;
        }
        List<Long> appIds = chatapps.stream()
            .map(AiMuseumAppVo::getId)
            .filter(Objects::nonNull)
            .toList();
        if (appIds.isEmpty()) {
            return;
        }
        Map<Long, String> appNameMap = chatAppMapper.selectByIds(appIds).stream()
            .collect(Collectors.toMap(ChatApp::getId, ChatApp::getAppName, (a, b) -> a));
        chatapps.forEach(app -> app.setAppName(appNameMap.get(app.getId())));
    }

    /**
     * 填充是否已过期：0未过期，1已过期
     */
    private void fillExpire(AiMuseumVo vo) {
        if (vo.getEndTime() != null && vo.getEndTime() > 0
            && System.currentTimeMillis() / 1000 > vo.getEndTime()) {
            vo.setIsExpire(1);
        } else {
            vo.setIsExpire(0);
        }
    }
}
