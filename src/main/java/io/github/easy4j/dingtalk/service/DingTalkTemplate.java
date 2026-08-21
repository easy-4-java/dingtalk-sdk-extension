package io.github.easy4j.dingtalk.service;

import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkConfigProvider;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.impl.DefaultDingTalkService;

/**
 * @deprecated Use {@link DefaultDingTalkService} directly; this type only preserved for
 *             source compatibility during the namespace migration.
 */
@Deprecated
public class DingTalkTemplate extends DefaultDingTalkService {
    public DingTalkTemplate(DingTalkConfigProvider configProvider, DingTalkAccessTokenProvider tokenProvider) {
        super(configProvider, tokenProvider);
    }
    public DingTalkTemplate(DingTalkConfig config, DingTalkAccessTokenProvider tokenProvider) {
        super(new DefaultDingTalkConfigProvider(config), tokenProvider);
    }
}
