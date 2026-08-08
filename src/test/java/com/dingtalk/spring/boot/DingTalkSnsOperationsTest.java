package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkSnsOperationsTest {

    private DingTalkSnsOperations createOperations() {
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
        return new DingTalkSnsOperations(template);
    }

    @Test
    void shouldCreateInstance() {
        DingTalkSnsOperations ops = createOperations();
        assertNotNull(ops);
    }
}
