package io.github.easy4j.dingtalk.service;

import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkSnsServiceTest {

    private DingTalkSnsService createOperations() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");

        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) { return "token"; }
            @Override
            public String getSnsAccessToken(String corpId, String appId) { return "sns_token"; }
        };

        DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);
        return new DingTalkSnsService(template);
    }

    @Test
    void shouldCreateInstance() {
        DingTalkSnsService ops = createOperations();
        assertNotNull(ops);
    }
}
