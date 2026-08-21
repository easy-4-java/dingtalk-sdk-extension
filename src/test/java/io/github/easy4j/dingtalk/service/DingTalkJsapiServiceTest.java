package io.github.easy4j.dingtalk.service;

import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkJsapiServiceTest {

    private DingTalkJsapiService createOperations() {
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
        return new DingTalkJsapiService(template);
    }

    @Test
    void shouldCreateInstance() {
        DingTalkJsapiService ops = createOperations();
        assertNotNull(ops);
    }

    @Test
    void shouldHaveClient() {
        DingTalkJsapiService ops = createOperations();
        assertNotNull(ops.client);
    }
}
