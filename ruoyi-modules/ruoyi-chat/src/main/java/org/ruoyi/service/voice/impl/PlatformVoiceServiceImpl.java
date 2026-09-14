package org.ruoyi.service.voice.impl;

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
import org.ruoyi.domain.bo.voice.PlatformVoiceBo;
import org.ruoyi.domain.entity.voice.PlatformVoice;
import org.ruoyi.domain.entity.voice.VoiceProfile;
import org.ruoyi.domain.vo.voice.PlatformVoiceVo;
import org.ruoyi.mapper.voice.PlatformVoiceMapper;
import org.ruoyi.mapper.voice.VoiceProfileMapper;
import org.ruoyi.service.voice.IPlatformVoiceService;
import org.ruoyi.service.voice.tts.TtsProviderRouter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 平台音色字典Service业务层处理
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PlatformVoiceServiceImpl implements IPlatformVoiceService {

    private final PlatformVoiceMapper baseMapper;
    private final VoiceProfileMapper voiceProfileMapper;
    private final TtsProviderRouter ttsProviderRouter;

    @Override
    public PlatformVoiceVo queryById(Long id) {
        PlatformVoiceVo vo = baseMapper.selectVoById(id);
        fillPlatformName(vo);
        return vo;
    }

    @Override
    public TableDataInfo<PlatformVoiceVo> queryPageList(PlatformVoiceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PlatformVoice> lqw = buildQueryWrapper(bo);
        Page<PlatformVoiceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillPlatformName(result.getRecords());
        return TableDataInfo.build(result);
    }

    @Override
    public List<PlatformVoiceVo> queryList(PlatformVoiceBo bo) {
        LambdaQueryWrapper<PlatformVoice> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(PlatformVoice::getId);
        List<PlatformVoiceVo> list = baseMapper.selectVoList(lqw);
        fillPlatformName(list);
        return list;
    }

    private LambdaQueryWrapper<PlatformVoice> buildQueryWrapper(PlatformVoiceBo bo) {
        LambdaQueryWrapper<PlatformVoice> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), PlatformVoice::getPlatform, bo.getPlatform());
        lqw.like(StringUtils.isNotBlank(bo.getVoiceName()), PlatformVoice::getVoiceName, bo.getVoiceName());
        lqw.like(StringUtils.isNotBlank(bo.getVoiceCode()), PlatformVoice::getVoiceCode, bo.getVoiceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getGender()), PlatformVoice::getGender, bo.getGender());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PlatformVoice::getStatus, bo.getStatus());
        return lqw;
    }

    @Override
    public Boolean insertByBo(PlatformVoiceBo bo) {
        checkUnique(bo);
        PlatformVoice add = MapstructUtils.convert(bo, PlatformVoice.class);
        return baseMapper.insert(add) > 0;
    }

    @Override
    public Boolean updateByBo(PlatformVoiceBo bo) {
        checkUnique(bo);
        PlatformVoice update = MapstructUtils.convert(bo, PlatformVoice.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 同平台下音色编码唯一校验
     */
    private void checkUnique(PlatformVoiceBo bo) {
        LambdaQueryWrapper<PlatformVoice> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformVoice::getPlatform, bo.getPlatform());
        lqw.eq(PlatformVoice::getVoiceCode, bo.getVoiceCode());
        lqw.ne(bo.getId() != null, PlatformVoice::getId, bo.getId());
        if (baseMapper.selectCount(lqw) > 0) {
            throw new IllegalArgumentException("该平台下音色编码已存在: " + bo.getVoiceCode());
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            LambdaQueryWrapper<VoiceProfile> lqw = Wrappers.lambdaQuery();
            lqw.in(VoiceProfile::getPlatformVoiceId, ids);
            if (voiceProfileMapper.selectCount(lqw) > 0) {
                throw new IllegalArgumentException("所选平台音色已被AI语音档案引用，请先移除引用后再删除");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 填充平台显示名
     */
    private void fillPlatformName(List<PlatformVoiceVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(this::fillPlatformName);
    }

    private void fillPlatformName(PlatformVoiceVo vo) {
        if (vo == null || StringUtils.isBlank(vo.getPlatform())) {
            return;
        }
        vo.setPlatformName(ttsProviderRouter.platformName(vo.getPlatform()));
    }

}
