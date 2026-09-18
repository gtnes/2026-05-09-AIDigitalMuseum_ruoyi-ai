package org.ruoyi.controller.chat;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.vo.chat.MuseumUsageSummaryVo;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI博物馆用量计费统计
 * <p>
 * 数据来源：博物馆C端对话（token计费）与语音合成（字符计费）流水
 *
 * @author gtnes
 * @date 2026-09-18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/museumUsage")
public class AiMuseumUsageController extends BaseController {

    private final IAiMuseumUsageService aiMuseumUsageService;

    /**
     * 按博物馆+业务类型汇总用量与费用（支持博物馆/业务类型/日期范围筛选）
     */
    @SaCheckPermission("system:museumUsage:query")
    @GetMapping("/summary")
    public R<List<MuseumUsageSummaryVo>> summary(MuseumUsageQueryBo bo) {
        return R.ok(aiMuseumUsageService.summary(bo));
    }

}
