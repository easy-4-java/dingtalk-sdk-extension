package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDingTalkConfigProviderTest {

    @Test
    void shouldReturnDingTalkProperties() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("corp1");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals(props, provider.getDingTalkProperties("corp1"));
    }

    @Test
    void shouldReturnCorpAppPropertiesByAgentId() {
        DingTalkCorpAppProperties app = new DingTalkCorpAppProperties();
        app.setAgentId("agent1");
        app.setAppKey("key1");
        app.setAppSecret("secret1");

        DingTalkProperties props = new DingTalkProperties();
        props.setCorpApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        DingTalkCorpAppProperties result = provider.getDingTalkCorpAppProperties("corp1", "agent1");
        assertNotNull(result);
        assertEquals("key1", result.getAppKey());
    }

    @Test
    void shouldReturnNullWhenCorpAppsEmpty() {
        DingTalkProperties props = new DingTalkProperties();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkCorpAppProperties("corp1", "agent1"));
    }

    @Test
    void shouldReturnNullWhenAgentIdNotFound() {
        DingTalkCorpAppProperties app = new DingTalkCorpAppProperties();
        app.setAgentId("agent1");

        DingTalkProperties props = new DingTalkProperties();
        props.setCorpApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkCorpAppProperties("corp1", "unknown"));
    }

    @Test
    void shouldReturnPersonalMiniAppProperties() {
        DingTalkPersonalMiniAppProperties app = new DingTalkPersonalMiniAppProperties();
        app.setAppId("app1");
        app.setAppSecret("secret1");

        DingTalkProperties props = new DingTalkProperties();
        props.setApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkPersonalMiniAppProperties("corp1", "app1"));
    }

    @Test
    void shouldReturnNullWhenAppsEmpty() {
        DingTalkProperties props = new DingTalkProperties();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkPersonalMiniAppProperties("corp1", "app1"));
    }

    @Test
    void shouldReturnNullWhenAppIdNotFound() {
        DingTalkPersonalMiniAppProperties app = new DingTalkPersonalMiniAppProperties();
        app.setAppId("app1");

        DingTalkProperties props = new DingTalkProperties();
        props.setApps(List.of(app));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkPersonalMiniAppProperties("corp1", "unknown"));
    }

    @Test
    void shouldReturnSuiteProperties() {
        DingTalkSuiteProperties suite = new DingTalkSuiteProperties();
        suite.setSuiteId("suite1");

        DingTalkProperties props = new DingTalkProperties();
        props.setSuites(List.of(suite));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkSuiteProperties("corp1", "suite1"));
    }

    @Test
    void shouldReturnNullWhenSuitesEmpty() {
        DingTalkProperties props = new DingTalkProperties();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkSuiteProperties("corp1", "suite1"));
    }

    @Test
    void shouldReturnNullWhenSuiteIdNotFound() {
        DingTalkSuiteProperties suite = new DingTalkSuiteProperties();
        suite.setSuiteId("suite1");

        DingTalkProperties props = new DingTalkProperties();
        props.setSuites(List.of(suite));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkSuiteProperties("corp1", "unknown"));
    }

    @Test
    void shouldReturnLoginProperties() {
        DingTalkLoginProperties login = new DingTalkLoginProperties();
        login.setAppId("login1");

        DingTalkProperties props = new DingTalkProperties();
        props.setLogins(List.of(login));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkLoginProperties("corp1", "login1"));
    }

    @Test
    void shouldReturnNullWhenLoginsEmpty() {
        DingTalkProperties props = new DingTalkProperties();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkLoginProperties("corp1", "login1"));
    }

    @Test
    void shouldReturnNullWhenLoginIdNotFound() {
        DingTalkLoginProperties login = new DingTalkLoginProperties();
        login.setAppId("login1");

        DingTalkProperties props = new DingTalkProperties();
        props.setLogins(List.of(login));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkLoginProperties("corp1", "unknown"));
    }

    @Test
    void shouldReturnRobotProperties() {
        DingTalkRobotProperties robot = new DingTalkRobotProperties();
        robot.setRobotId("robot1");

        DingTalkProperties props = new DingTalkProperties();
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNotNull(provider.getDingTalkRobotProperties("corp1", "robot1"));
    }

    @Test
    void shouldReturnNullWhenRobotsEmpty() {
        DingTalkProperties props = new DingTalkProperties();
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkRobotProperties("corp1", "robot1"));
    }

    @Test
    void shouldReturnNullWhenRobotIdNotFound() {
        DingTalkRobotProperties robot = new DingTalkRobotProperties();
        robot.setRobotId("robot1");

        DingTalkProperties props = new DingTalkProperties();
        props.setRobots(List.of(robot));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertNull(provider.getDingTalkRobotProperties("corp1", "unknown"));
    }

    @Test
    void shouldReturnCorpId() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("corp1");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals("corp1", provider.getCorpId("anyKey"));
    }

    @Test
    void shouldReturnCorpSecret() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpSecret("secret");
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        assertEquals("secret", provider.getCorpSecret("corp1"));
    }

    @Test
    void shouldInitAndRegisterAppKeys() {
        DingTalkCorpAppProperties corpApp = new DingTalkCorpAppProperties();
        corpApp.setAppKey("key1");
        corpApp.setAppSecret("secret1");

        DingTalkPersonalMiniAppProperties miniApp = new DingTalkPersonalMiniAppProperties();
        miniApp.setAppId("appId1");
        miniApp.setAppSecret("secret2");

        DingTalkSuiteProperties suite = new DingTalkSuiteProperties();
        suite.setAppId("suiteAppId");
        suite.setSuiteSecret("suiteSecret");

        DingTalkLoginProperties login = new DingTalkLoginProperties();
        login.setAppId("loginAppId");
        login.setAppSecret("loginSecret");

        DingTalkProperties props = new DingTalkProperties();
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
        DingTalkCorpAppProperties corpApp = new DingTalkCorpAppProperties();
        corpApp.setAppKey("key1");
        corpApp.setAppSecret("secret1");

        DingTalkProperties props = new DingTalkProperties();
        props.setCorpApps(List.of(corpApp));

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        provider.init();

        assertEquals("secret1", provider.getAppSecret("corp1", "key1"));
        assertNull(provider.getAppSecret("corp1", "unknown"));
    }

    @Test
    void shouldHandleEmptyListsInInit() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpApps(new ArrayList<>());
        props.setApps(new ArrayList<>());
        props.setSuites(new ArrayList<>());
        props.setLogins(new ArrayList<>());

        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(props);
        provider.init();

        assertFalse(provider.hasAppKey("any"));
    }
}
