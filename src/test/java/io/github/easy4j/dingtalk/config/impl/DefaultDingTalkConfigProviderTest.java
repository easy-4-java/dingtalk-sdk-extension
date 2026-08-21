package io.github.easy4j.dingtalk.config.impl;

import io.github.easy4j.dingtalk.config.*; import io.github.easy4j.dingtalk.config.impl.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDingTalkConfigProviderTest {

    @Test
    void shouldReturnDingTalkProperties() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals(props, provider.getDingTalkConfig("corp1"));
    }

    @Test
    void shouldReturnCorpAppPropertiesByAgentId() {
        DingTalkCorpAppConfig app = new DingTalkCorpAppConfig();
        app.setAgentId("agent1");
        app.setAppKey("key1");
        app.setAppSecret("secret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        DingTalkCorpAppConfig result = provider.getDingTalkCorpAppConfig("corp1", "agent1");
        assertNotNull(result);
        assertEquals("key1", result.getAppKey());
    }

    @Test
    void shouldReturnNullWhenCorpAppsEmpty() {
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkCorpAppConfig("corp1", "agent1"));
    }

    @Test
    void shouldReturnNullWhenAgentIdNotFound() {
        DingTalkCorpAppConfig app = new DingTalkCorpAppConfig();
        app.setAgentId("agent1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkCorpAppConfig("corp1", "unknown"));
    }

    @Test
    void shouldReturnPersonalMiniAppProperties() {
        DingTalkPersonalMiniAppConfig app = new DingTalkPersonalMiniAppConfig();
        app.setAppId("app1");
        app.setAppSecret("secret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkPersonalMiniAppConfig("corp1", "app1"));
    }

    @Test
    void shouldReturnNullWhenAppsEmpty() {
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkPersonalMiniAppConfig("corp1", "app1"));
    }

    @Test
    void shouldReturnNullWhenAppIdNotFound() {
        DingTalkPersonalMiniAppConfig app = new DingTalkPersonalMiniAppConfig();
        app.setAppId("app1");

        DingTalkConfig props = new DingTalkConfig();
        props.setApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkPersonalMiniAppConfig("corp1", "unknown"));
    }

    @Test
    void shouldReturnSuiteProperties() {
        DingTalkSuiteConfig suite = new DingTalkSuiteConfig();
        suite.setSuiteId("suite1");

        DingTalkConfig props = new DingTalkConfig();
        props.setSuites(List.of(suite));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkSuiteConfig("corp1", "suite1"));
    }

    @Test
    void shouldReturnNullWhenSuitesEmpty() {
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkSuiteConfig("corp1", "suite1"));
    }

    @Test
    void shouldReturnNullWhenSuiteIdNotFound() {
        DingTalkSuiteConfig suite = new DingTalkSuiteConfig();
        suite.setSuiteId("suite1");

        DingTalkConfig props = new DingTalkConfig();
        props.setSuites(List.of(suite));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkSuiteConfig("corp1", "unknown"));
    }

    @Test
    void shouldReturnLoginProperties() {
        DingTalkLoginConfig login = new DingTalkLoginConfig();
        login.setAppId("login1");

        DingTalkConfig props = new DingTalkConfig();
        props.setLogins(List.of(login));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkLoginConfig("corp1", "login1"));
    }

    @Test
    void shouldReturnNullWhenLoginsEmpty() {
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkLoginConfig("corp1", "login1"));
    }

    @Test
    void shouldReturnNullWhenLoginIdNotFound() {
        DingTalkLoginConfig login = new DingTalkLoginConfig();
        login.setAppId("login1");

        DingTalkConfig props = new DingTalkConfig();
        props.setLogins(List.of(login));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkLoginConfig("corp1", "unknown"));
    }

    @Test
    void shouldReturnRobotProperties() {
        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot1");

        DingTalkConfig props = new DingTalkConfig();
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkRobotConfig("corp1", "robot1"));
    }

    @Test
    void shouldReturnNullWhenRobotsEmpty() {
        DingTalkConfig props = new DingTalkConfig();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkRobotConfig("corp1", "robot1"));
    }

    @Test
    void shouldReturnNullWhenRobotIdNotFound() {
        DingTalkRobotConfig robot = new DingTalkRobotConfig();
        robot.setRobotId("robot1");

        DingTalkConfig props = new DingTalkConfig();
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkRobotConfig("corp1", "unknown"));
    }

    @Test
    void shouldReturnCorpId() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals("corp1", provider.getCorpId("anyKey"));
    }

    @Test
    void shouldReturnCorpSecret() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpSecret("secret");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals("secret", provider.getCorpSecret("corp1"));
    }

    @Test
    void shouldInitAndRegisterAppKeys() {
        DingTalkCorpAppConfig corpApp = new DingTalkCorpAppConfig();
        corpApp.setAppKey("key1");
        corpApp.setAppSecret("secret1");

        DingTalkPersonalMiniAppConfig miniApp = new DingTalkPersonalMiniAppConfig();
        miniApp.setAppId("appId1");
        miniApp.setAppSecret("secret2");

        DingTalkSuiteConfig suite = new DingTalkSuiteConfig();
        suite.setAppId("suiteAppId");
        suite.setSuiteSecret("suiteSecret");

        DingTalkLoginConfig login = new DingTalkLoginConfig();
        login.setAppId("loginAppId");
        login.setAppSecret("loginSecret");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpApps(List.of(corpApp));
        props.setApps(List.of(miniApp));
        props.setSuites(List.of(suite));
        props.setLogins(List.of(login));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        provider.init();

        assertTrue(provider.hasAppKey("key1"));
        assertTrue(provider.hasAppKey("appId1"));
        assertTrue(provider.hasAppKey("suiteAppId"));
        assertTrue(provider.hasAppKey("loginAppId"));
        assertFalse(provider.hasAppKey("unknown"));
    }

    @Test
    void shouldReturnAppSecret() {
        DingTalkCorpAppConfig corpApp = new DingTalkCorpAppConfig();
        corpApp.setAppKey("key1");
        corpApp.setAppSecret("secret1");

        DingTalkConfig props = new DingTalkConfig();
        props.setCorpApps(List.of(corpApp));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        provider.init();

        assertEquals("secret1", provider.getAppSecret("corp1", "key1"));
        assertNull(provider.getAppSecret("corp1", "unknown"));
    }

    @Test
    void shouldHandleEmptyListsInInit() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpApps(new ArrayList<>());
        props.setApps(new ArrayList<>());
        props.setSuites(new ArrayList<>());
        props.setLogins(new ArrayList<>());

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        provider.init();

        assertFalse(provider.hasAppKey("any"));
    }
}
