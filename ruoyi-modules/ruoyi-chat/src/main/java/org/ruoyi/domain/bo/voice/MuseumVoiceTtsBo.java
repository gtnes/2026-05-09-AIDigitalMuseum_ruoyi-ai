package org.ruoyi.domain.bo.voice;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博物馆C端语音合成请求业务对象（公开接口，无需登录）
 * <p>
 * 继承VoiceTtsBo保留HMAC签名三件套（TtsRequestGuard校验），
 * 后端额外校验：博物馆存在且启用、服务未到期、音色已在该博物馆智能体中配置
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MuseumVoiceTtsBo extends VoiceTtsBo {

    /**
     * 博物馆ID（ai_museum.id）
     */
    @NotNull(message = "博物馆ID不能为空")
    private Long museumId;

}
