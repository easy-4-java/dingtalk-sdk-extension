package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.config.*;
import com.taobao.api.ApiException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkTemplateTest {

    private DingTalkTemplate createTemplate() {
        DingTalkCorpAppConfig corpApp = new DingTalkCorpAppConfig();
        corpApp.setAgentId("agent1");
        corpApp.setAppKey("key1");
        corpApp.setAppSecret("secret1");

        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot1");
        robot.setAccessToken("token1");
        robot.setSecretToken("SECsecret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        props.setCorpSecret("corpSecret1");
        props.setCorpApps(List.of(corpApp));
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider configProvider = new DefaultDingTalkConfigProvider(props);
        configProvider.init();

        DingTalkAccessTokenProvider tokenProvider = new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) {
                return "mock_token";
            }
            @Override
            public String getSnsAccessToken(String corpId, String appId) {
                return "mock_sns_token";
            }
        };

        return new DingTalkTemplate(configProvider, tokenProvider);
    }

    @Test
    void shouldCheckHasAppKey() {
        DingTalkTemplate template = createTemplate();
        assertTrue(template.hasAppKey("key1"));
        assertFalse(template.hasAppKey("unknown"));
    }

    @Test
    void shouldGetCorpId() {
        DingTalkTemplate template = createTemplate();
        assertEquals("corp1", template.getCorpId("key1"));
    }

    @Test
    void shouldGetCorpSecret() {
        DingTalkTemplate template = createTemplate();
        assertEquals("corpSecret1", template.getCorpSecret("corp1"));
    }

    @Test
    void shouldGetAppSecret() {
        DingTalkTemplate template = createTemplate();
        assertEquals("secret1", template.getAppSecret("corp1", "key1"));
    }

    @Test
    void shouldGetAccessToken() throws ApiException {
        DingTalkTemplate template = createTemplate();
        assertEquals("mock_token", template.getAccessToken("corp1", "key1"));
    }

    @Test
    void shouldGetSnsAccessToken() throws ApiException {
        DingTalkTemplate template = createTemplate();
        assertEquals("mock_sns_token", template.getSnsAccessToken("corp1", "appId"));
    }

    @Test
    void shouldComputeSignature() {
        DingTalkTemplate template = createTemplate();
        String sign = template.getSign("SECtest", 1234567890L);
        assertNotNull(sign);
        assertFalse(sign.isEmpty());
    }

    @Test
    void shouldReturnOpsForAccount() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForAccount());
    }

    @Test
    void shouldReturnOpsForSns() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForSns());
    }

    @Test
    void shouldReturnOpsForSso() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForSso());
    }

    @Test
    void shouldReturnOpsForJsapi() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForJsapi());
    }

    @Test
    void shouldReturnOpsForRobot() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForRobot());
    }

    @Test
    void shouldReturnOpsForUser() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.opsForUser());
    }

    @Test
    void shouldReturnConfigProvider() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.getDingTalkConfigProvider());
    }

    @Test
    void shouldReturnTokenProvider() {
        DingTalkTemplate template = createTemplate();
        assertNotNull(template.getDingTalkAccessTokenProvider());
    }
}
