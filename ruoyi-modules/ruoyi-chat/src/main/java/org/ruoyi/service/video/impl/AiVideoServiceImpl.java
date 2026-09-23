package org.ruoyi.service.video.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.video.AiVideoBo;
import org.ruoyi.domain.entity.video.AiVideo;
import org.ruoyi.domain.entity.video.AiVideoCategory;
import org.ruoyi.domain.vo.video.AiVideoCategoryVo;
import org.ruoyi.domain.vo.video.AiVideoFrontVo;
import org.ruoyi.domain.vo.video.AiVideoVo;
import org.ruoyi.mapper.video.AiVideoCategoryMapper;
import org.ruoyi.mapper.video.AiVideoMapper;
import org.ruoyi.service.video.IAiVideoService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI视频Service业务层处理
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiVideoServiceImpl implements IAiVideoService {

    private final AiVideoMapper baseMapper;
    private final AiVideoCategoryMapper categoryMapper;

    /**
     * 查询AI视频
     *
     * @param id 主键
     * @return AI视频
     */
    @Override
    public AiVideoVo queryById(Long id) {
        AiVideoVo vo = baseMapper.selectVoById(id);
        fillCategoryName(vo == null ? List.of() : List.of(vo));
        return vo;
    }

    /**
     * 前台查询指定分类下的视频列表（仅启用中的视频，供博物馆C端展示）
     */
    @Override
    public List<AiVideoFrontVo> frontListByCategory(Long categoryId) {
        LambdaQueryWrapper<AiVideo> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiVideo::getCategoryId, categoryId);
        // status沿用sys_normal_disable约定：0正常 1停用
        lqw.eq(AiVideo::getStatus, "0");
        // 置顶优先，再按显示顺序、id排列
        lqw.orderByDesc(AiVideo::getTopFlag).orderByAsc(AiVideo::getSort).orderByAsc(AiVideo::getId);
        List<AiVideo> list = baseMapper.selectList(lqw);
        return MapstructUtils.convert(list, AiVideoFrontVo.class);
    }

    /**
     * 前台查询视频详情（仅启用中的视频，供博物馆C端播放页使用）
     */
    @Override
    public AiVideoFrontVo frontQueryById(Long id) {
        AiVideo entity = baseMapper.selectById(id);
        // 前台仅展示启用状态（0正常）的视频
        if (entity == null || !"0".equals(entity.getStatus())) {
            return null;
        }
        return MapstructUtils.convert(entity, AiVideoFrontVo.class);
    }

    /**
     * 分页查询AI视频列表
     */
    @Override
    public TableDataInfo<AiVideoVo> queryPageList(AiVideoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiVideo> lqw = buildQueryWrapper(bo);
        Page<AiVideoVo> result = baseMapper.selectPageVideoList(pageQuery.build(), lqw);
        fillCategoryName(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI视频列表
     */
    @Override
    public List<AiVideoVo> queryList(AiVideoBo bo) {
        LambdaQueryWrapper<AiVideo> lqw = buildQueryWrapper(bo);
        List<AiVideoVo> list = baseMapper.selectVideoList(lqw);
        fillCategoryName(list);
        return list;
    }

    private LambdaQueryWrapper<AiVideo> buildQueryWrapper(AiVideoBo bo) {
        LambdaQueryWrapper<AiVideo> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), AiVideo::getTitle, bo.getTitle());
        lqw.eq(bo.getCategoryId() != null, AiVideo::getCategoryId, bo.getCategoryId());
        lqw.eq(StringUtils.isNotBlank(bo.getShowCategory()), AiVideo::getShowCategory, bo.getShowCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiVideo::getStatus, bo.getStatus());
        // 置顶优先，再按显示顺序、id排列
        lqw.orderByDesc(AiVideo::getTopFlag).orderByAsc(AiVideo::getSort).orderByAsc(AiVideo::getId);
        return lqw;
    }

    /**
     * 新增AI视频
     */
    @Override
    public Boolean insertByBo(AiVideoBo bo) {
        checkCategoryValid(bo);
        AiVideo add = MapstructUtils.convert(bo, AiVideo.class);
        if (add.getPlayCount() == null) {
            add.setPlayCount(0L);
        }
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改AI视频
     */
    @Override
    public Boolean updateByBo(AiVideoBo bo) {
        checkCategoryValid(bo);
        AiVideo update = MapstructUtils.convert(bo, AiVideo.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验所选分类存在（停用分类允许引用，避免影响已发布内容编辑）
     */
    private void checkCategoryValid(AiVideoBo bo) {
        if (bo.getCategoryId() == null) {
            return;
        }
        AiVideoCategoryVo category = categoryMapper.selectVoById(bo.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("所选视频分类不存在");
        }
    }

    /**
     * 校验并批量删除AI视频信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量填充分类名称（冗余显示）
     */
    private void fillCategoryName(List<AiVideoVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> categoryIds = list.stream()
            .map(AiVideoVo::getCategoryId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, AiVideoCategoryVo> categoryMap = categoryIds.isEmpty() ? Map.of() :
            categoryMapper.selectVoList(Wrappers.lambdaQuery(AiVideoCategory.class)
                    .in(AiVideoCategory::getId, categoryIds))
                .stream().collect(Collectors.toMap(AiVideoCategoryVo::getId, Function.identity()));
        for (AiVideoVo vo : list) {
            AiVideoCategoryVo category = categoryMap.get(vo.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
    }

}
