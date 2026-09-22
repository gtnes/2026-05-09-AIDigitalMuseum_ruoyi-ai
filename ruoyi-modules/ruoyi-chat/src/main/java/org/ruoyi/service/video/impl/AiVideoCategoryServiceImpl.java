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
import org.ruoyi.domain.bo.video.AiVideoCategoryBo;
import org.ruoyi.domain.entity.video.AiVideo;
import org.ruoyi.domain.entity.video.AiVideoCategory;
import org.ruoyi.domain.vo.video.AiVideoCategoryVo;
import org.ruoyi.mapper.video.AiVideoCategoryMapper;
import org.ruoyi.mapper.video.AiVideoMapper;
import org.ruoyi.service.video.IAiVideoCategoryService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * AI视频分类Service业务层处理
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiVideoCategoryServiceImpl implements IAiVideoCategoryService {

    private final AiVideoCategoryMapper baseMapper;
    private final AiVideoMapper aiVideoMapper;

    /**
     * 查询AI视频分类
     *
     * @param id 主键
     * @return AI视频分类
     */
    @Override
    public AiVideoCategoryVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI视频分类列表
     */
    @Override
    public TableDataInfo<AiVideoCategoryVo> queryPageList(AiVideoCategoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiVideoCategory> lqw = buildQueryWrapper(bo);
        Page<AiVideoCategoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI视频分类列表
     */
    @Override
    public List<AiVideoCategoryVo> queryList(AiVideoCategoryBo bo) {
        LambdaQueryWrapper<AiVideoCategory> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(AiVideoCategory::getSort).orderByAsc(AiVideoCategory::getId);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询启用中的分类下拉选项（按sort排序）
     */
    @Override
    public List<AiVideoCategoryVo> queryOptions() {
        LambdaQueryWrapper<AiVideoCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiVideoCategory::getStatus, "0");
        lqw.orderByAsc(AiVideoCategory::getSort).orderByAsc(AiVideoCategory::getId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiVideoCategory> buildQueryWrapper(AiVideoCategoryBo bo) {
        LambdaQueryWrapper<AiVideoCategory> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCategoryName()), AiVideoCategory::getCategoryName, bo.getCategoryName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), AiVideoCategory::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增AI视频分类
     */
    @Override
    public Boolean insertByBo(AiVideoCategoryBo bo) {
        checkUnique(bo);
        AiVideoCategory add = MapstructUtils.convert(bo, AiVideoCategory.class);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改AI视频分类
     */
    @Override
    public Boolean updateByBo(AiVideoCategoryBo bo) {
        checkUnique(bo);
        AiVideoCategory update = MapstructUtils.convert(bo, AiVideoCategory.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 分类名称唯一校验
     */
    private void checkUnique(AiVideoCategoryBo bo) {
        LambdaQueryWrapper<AiVideoCategory> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiVideoCategory::getCategoryName, bo.getCategoryName());
        lqw.ne(bo.getId() != null, AiVideoCategory::getId, bo.getId());
        if (baseMapper.selectCount(lqw) > 0) {
            throw new IllegalArgumentException("分类名称已存在: " + bo.getCategoryName());
        }
    }

    /**
     * 校验并批量删除AI视频分类信息（被视频引用的分类不允许删除）
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid && CollUtil.isNotEmpty(ids)) {
            LambdaQueryWrapper<AiVideo> lqw = Wrappers.lambdaQuery();
            lqw.in(AiVideo::getCategoryId, ids);
            if (aiVideoMapper.selectCount(lqw) > 0) {
                throw new IllegalArgumentException("所选分类已被AI视频引用，请先移除引用后再删除");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

}
