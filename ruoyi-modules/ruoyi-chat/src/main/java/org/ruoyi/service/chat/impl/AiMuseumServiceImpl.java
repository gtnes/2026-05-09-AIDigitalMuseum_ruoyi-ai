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
import org.ruoyi.domain.vo.chat.AiMuseumAppFrontVo;
import org.ruoyi.domain.vo.chat.AiMuseumAppVo;
import org.ruoyi.domain.vo.chat.AiMuseumFrontVo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;
import org.ruoyi.mapper.chat.AiMuseumMapper;
import org.ruoyi.mapper.chat.ChatAppMapper;
import org.ruoyi.service.chat.IAiMuseumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
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
     * 前台查询AI博物馆实例（仅返回H5展示所需字段，图片ossId转URL由序列化阶段处理）
     *
     * @param id 主键
     * @return AI博物馆前台展示视图对象，实例不存在或已停用时返回 null
     */
    @Override
    public AiMuseumFrontVo frontQueryById(Long id) {
        AiMuseumVo vo = queryById(id);
        // 前台仅展示启用状态的实例
        if (vo == null || vo.getStatus() == null || vo.getStatus() != 1) {
            return null;
        }
        AiMuseumFrontVo front = new AiMuseumFrontVo();
        front.setId(vo.getId());
        front.setTitle(vo.getTitle());
        front.setLogoUrl(vo.getLogoUrl());
        front.setBannerUrl(vo.getBannerUrl());
        front.setIsExpire(vo.getIsExpire());
        front.setExpireTips(vo.getExpireTips());
        front.setVrEnable(vo.getVrEnable());
        front.setVrUrl(vo.getVrUrl());
        front.setVideoEnable(vo.getVideoEnable());
        front.setVideoCategoryId(vo.getVideoCategoryId());
        front.setVideoChatappId(vo.getVideoChatappId());
        if (CollUtil.isNotEmpty(vo.getChatapps())) {
            // 批量查询智能体的描述与图标（appDescribe/appShow来自chat_app，不落库）
            List<Long> appIds = vo.getChatapps().stream()
                .map(AiMuseumAppVo::getId)
                .filter(Objects::nonNull)
                .toList();
            Map<Long, ChatApp> appMap = appIds.isEmpty() ? Map.of() : chatAppMapper.selectByIds(appIds).stream()
                .collect(Collectors.toMap(ChatApp::getId, app -> app, (a, b) -> a));
            // 按展示排序升序，未设置的排在后面
            List<AiMuseumAppFrontVo> apps = vo.getChatapps().stream()
                .sorted(Comparator.comparing(AiMuseumAppVo::getSort,
                    Comparator.nullsLast(Comparator.naturalOrder())))
                .map(appVo -> toFrontAppVo(appVo, appMap.get(appVo.getId())))
                .toList();
            front.setChatapps(apps);
        }
        return front;
    }

    /**
     * 校验博物馆服务可用性（存在、启用、未到期），不通过时抛出ServiceException
     * 供博物馆C端公开接口（museumSend/museumTTS）做准入校验
     *
     * @param museumId 博物馆ID
     * @return 博物馆实例（含智能体配置列表，用于后续绑定校验）
     */
    @Override
    public AiMuseumVo checkServiceValid(Long museumId) {
        AiMuseumVo vo = queryById(museumId);
        if (vo == null) {
            throw new ServiceException("博物馆不存在");
        }
        if (vo.getStatus() == null || vo.getStatus() != 1) {
            throw new ServiceException("博物馆服务已停用");
        }
        // 到期判定与fillExpire一致：endTime有效且当前时间已超过
        if (vo.getEndTime() != null && vo.getEndTime() > 0
            && System.currentTimeMillis() / 1000 > vo.getEndTime()) {
            throw new ServiceException("当前服务已到期");
        }
        return vo;
    }

    /**
     * 管理端智能体配置VO转前台展示VO
     *
     * @param appVo   博物馆配置中的智能体信息
     * @param chatApp 对应的智能体（可能已被删除，为null时跳过描述与图标填充）
     */
    private AiMuseumAppFrontVo toFrontAppVo(AiMuseumAppVo appVo, ChatApp chatApp) {
        AiMuseumAppFrontVo frontApp = new AiMuseumAppFrontVo();
        frontApp.setId(appVo.getId());
        frontApp.setAppName(appVo.getAppName());
        frontApp.setBgUrl(appVo.getBgUrl());
        frontApp.setIdleImgUrl(appVo.getIdleImgUrl());
        frontApp.setTalkingGifUrl(appVo.getTalkingGifUrl());
        frontApp.setDescription(appVo.getDescription());
        frontApp.setVoiceProfileId(appVo.getVoiceProfileId());
        frontApp.setVoiceEnabled(appVo.getVoiceEnabled());
        frontApp.setVoiceAutoPlay(appVo.getVoiceAutoPlay());
        frontApp.setDuty(appVo.getDuty());
        frontApp.setSort(appVo.getSort());
        if (chatApp != null) {
            frontApp.setAppDescribe(chatApp.getAppDescribe());
            frontApp.setAppShow(chatApp.getAppShow());
        }
        return frontApp;
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
        Page<AiMuseumVo> result = baseMapper.selectPageMuseumList(pageQuery.build(), lqw);
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
        List<AiMuseumVo> list = baseMapper.selectMuseumList(lqw);
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
