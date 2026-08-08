package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkSsoOperationsTest {

    private DingTalkSsoOperations createOperations() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("corp1");

        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) { return "token"; }
            @Override
            public String getSnsAccessToken(String corpId, String appId) { return "sns_token"; }
        };

        DingTalkTemplate template = new DingTalkTemplate(configProvider, tokenProvider);
        return new DingTalkSsoOperations(template);
    }

    @Test
    void shouldCreateInstance() {
        DingTalkSsoOperations ops = createOperations();
        assertNotNull(ops);
    }
}
