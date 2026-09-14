package org.ruoyi.service.voice.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.chat.domain.vo.chat.ChatModelVo;
import org.ruoyi.common.chat.entity.chat.ChatModel;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.voice.VoiceProfileBo;
import org.ruoyi.domain.entity.voice.PlatformVoice;
import org.ruoyi.domain.entity.voice.VoiceProfile;
import org.ruoyi.domain.vo.voice.PlatformVoiceVo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;
import org.ruoyi.mapper.chat.ChatModelMapper;
import org.ruoyi.mapper.voice.PlatformVoiceMapper;
import org.ruoyi.mapper.voice.VoiceProfileMapper;
import org.ruoyi.service.voice.IVoiceProfileService;
import org.ruoyi.service.voice.tts.TtsProviderRouter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI语音音色档案Service业务层处理
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VoiceProfileServiceImpl implements IVoiceProfileService {

    private final VoiceProfileMapper baseMapper;
    private final PlatformVoiceMapper platformVoiceMapper;
    private final ChatModelMapper chatModelMapper;
    private final TtsProviderRouter ttsProviderRouter;

    @Override
    public VoiceProfileVo queryById(Long id) {
        VoiceProfileVo vo = baseMapper.selectVoById(id);
        fillRelationInfo(vo == null ? List.of() : List.of(vo));
        return vo;
    }

    @Override
    public TableDataInfo<VoiceProfileVo> queryPageList(VoiceProfileBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<VoiceProfile> lqw = buildQueryWrapper(bo);
        Page<VoiceProfileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillRelationInfo(result.getRecords());
        return TableDataInfo.build(result);
    }

    @Override
    public List<VoiceProfileVo> queryList(VoiceProfileBo bo) {
        LambdaQueryWrapper<VoiceProfile> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(VoiceProfile::getSort).orderByAsc(VoiceProfile::getId);
        List<VoiceProfileVo> list = baseMapper.selectVoList(lqw);
        fillRelationInfo(list);
        return list;
    }

    private LambdaQueryWrapper<VoiceProfile> buildQueryWrapper(VoiceProfileBo bo) {
        LambdaQueryWrapper<VoiceProfile> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getVoiceName()), VoiceProfile::getVoiceName, bo.getVoiceName());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), VoiceProfile::getPlatform, bo.getPlatform());
        lqw.eq(bo.getPlatformVoiceId() != null, VoiceProfile::getPlatformVoiceId, bo.getPlatformVoiceId());
        lqw.eq(bo.getModelId() != null, VoiceProfile::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), VoiceProfile::getStatus, bo.getStatus());
        return lqw;
    }

    @Override
    public Boolean insertByBo(VoiceProfileBo bo) {
        checkPlatformVoiceValid(bo);
        VoiceProfile add = MapstructUtils.convert(bo, VoiceProfile.class);
        return baseMapper.insert(add) > 0;
    }

    @Override
    public Boolean updateByBo(VoiceProfileBo bo) {
        checkPlatformVoiceValid(bo);
        VoiceProfile update = MapstructUtils.convert(bo, VoiceProfile.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验所选平台音色存在且平台标识一致
     */
    private void checkPlatformVoiceValid(VoiceProfileBo bo) {
        PlatformVoiceVo platformVoice = platformVoiceMapper.selectVoById(bo.getPlatformVoiceId());
        if (platformVoice == null) {
            throw new IllegalArgumentException("所选平台音色不存在");
        }
        if (!platformVoice.getPlatform().equals(bo.getPlatform())) {
            throw new IllegalArgumentException("所选平台音色与平台标识不一致");
        }
        if (!"0".equals(platformVoice.getStatus())) {
            throw new IllegalArgumentException("所选平台音色已停用");
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量填充冗余显示信息：平台音色编码/名称、关联模型名称、平台显示名
     */
    private void fillRelationInfo(List<VoiceProfileVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> voiceIds = list.stream()
            .map(VoiceProfileVo::getPlatformVoiceId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, PlatformVoiceVo> voiceMap = voiceIds.isEmpty() ? Map.of() :
            platformVoiceMapper.selectVoList(Wrappers.lambdaQuery(PlatformVoice.class)
                    .in(PlatformVoice::getId, voiceIds))
                .stream().collect(Collectors.toMap(PlatformVoiceVo::getId, Function.identity()));

        Set<Long> modelIds = list.stream()
            .map(VoiceProfileVo::getModelId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, ChatModelVo> modelMap = modelIds.isEmpty() ? Map.of() :
            chatModelMapper.selectVoList(Wrappers.lambdaQuery(ChatModel.class)
                    .in(ChatModel::getId, modelIds))
                .stream().collect(Collectors.toMap(ChatModelVo::getId, Function.identity()));

        for (VoiceProfileVo vo : list) {
            if (StringUtils.isNotBlank(vo.getPlatform())) {
                vo.setPlatformName(ttsProviderRouter.platformName(vo.getPlatform()));
            }
            PlatformVoiceVo platformVoice = voiceMap.get(vo.getPlatformVoiceId());
            if (platformVoice != null) {
                vo.setPlatformVoiceCode(platformVoice.getVoiceCode());
                vo.setPlatformVoiceName(platformVoice.getVoiceName());
            }
            ChatModelVo chatModel = modelMap.get(vo.getModelId());
            if (chatModel != null) {
                vo.setModelName(chatModel.getModelName());
            }
        }
    }

}
