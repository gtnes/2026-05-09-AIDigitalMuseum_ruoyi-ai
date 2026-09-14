package org.ruoyi.service.voice;

import org.ruoyi.domain.bo.voice.VoiceTtsBo;
import org.ruoyi.domain.bo.voice.VoiceTtsPreviewBo;
import org.ruoyi.domain.vo.voice.VoiceTtsVo;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 语音合成Service接口
 *
 * @author ruoyi
 * @date 2026-09-14
 */
public interface IVoiceTtsService {

    /**
     * 按已保存的音色档案合成语音（C端聊天播报）
     *
     * @param bo 合成请求(音色ID+文本)
     * @return 合成结果(dataUrl)
     */
    VoiceTtsVo synthesize(VoiceTtsBo bo);

    /**
     * 试听合成（管理端编辑弹窗内即时试听，无需先保存档案）
     *
     * @param bo 试听请求(平台音色ID+模型ID+参数+文本)
     * @return 合成结果(dataUrl)
     */
    VoiceTtsVo preview(VoiceTtsPreviewBo bo);

    /**
     * 已支持的平台下拉选项 [{label: 平台名, value: 平台标识}]
     */
    List<LinkedHashMap<String, String>> platformOptions();

}
