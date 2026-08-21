package io.github.easy4j.dingtalk;

import io.github.easy4j.dingtalk.model.message.ActionCardMessage;
import io.github.easy4j.dingtalk.config.DingTalkConfig;
import io.github.easy4j.dingtalk.config.DingTalkCorpAppConfig;
import io.github.easy4j.dingtalk.config.DingTalkRobotConfig;
import io.github.easy4j.dingtalk.config.impl.DefaultDingTalkConfigProvider;
import io.github.easy4j.dingtalk.service.DingTalkAccessTokenProvider;
import io.github.easy4j.dingtalk.service.DingTalkTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrentMigrationBaselineTest {

    static DingTalkConfig config() {
        DingTalkConfig config = new DingTalkConfig();
        config.setCorpId("corp-1");
        config.setCorpSecret("corp-secret");
        return config;
    }

    static DingTalkConfig configWithCorpApp(String appKey, String appSecret) {
        DingTalkCorpAppConfig corpApp = new DingTalkCorpAppConfig();
        corpApp.setAgentId("agent-1");
        corpApp.setAppKey(appKey);
        corpApp.setAppSecret(appSecret);
        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot-1");
        robot.setAccessToken("access-token-1");
        robot.setSecretToken("SECsecret-1");
        DingTalkConfig config = config();
        config.setCorpApps(List.of(corpApp));
        config.setRobots(List.of(robot));
        return config;
    }

    static DefaultDingTalkConfigProvider configProvider() {
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(configWithCorpApp("app-key", "app-secret"));
        provider.init();
        return provider;
    }

    static DingTalkAccessTokenProvider tokenProvider() {
        return new DingTalkAccessTokenProvider() {
            @Override
            public String getAccessToken(String corpId, String appKey) {
                return "mock_access_token";
            }
            @Override
            public String getSnsAccessToken(String corpId, String appId) {
                return "mock_sns_token";
            }
        };
    }

    static DingTalkTemplate template() {
        return new DingTalkTemplate(configProvider(), tokenProvider());
    }

    @Test
    void templateConstructionMustNotThrowClassCastException() {
        DingTalkTemplate template = template();
        assertNotNull(template.opsForRobot());
        assertSame(template.opsForRobot(), template.opsForRobot());
    }

    @Test
    void actionCardModelProvidesTextAndButtonsAccessors() {
        ActionCardMessage message = new ActionCardMessage("title", "**body**");
        assertEquals("title", message.getTitle());
        assertEquals("**body**", message.getText());
        assertNotNull(message.getButtons());
        assertTrue(message.getButtons().isEmpty());
    }
}
