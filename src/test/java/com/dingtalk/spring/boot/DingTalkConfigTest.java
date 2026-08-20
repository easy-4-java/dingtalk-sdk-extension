package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.config.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkConfigTest {

    @Test
    void shouldHavePrefixConstant() {
        assertEquals("dingtalk", DingTalkConfig.PREFIX);
    }

    @Test
    void shouldSetAndGetCorpId() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpId("corp1");
        assertEquals("corp1", props.getCorpId());
    }

    @Test
    void shouldSetAndGetCorpSecret() {
        DingTalkConfig props = new DingTalkConfig();
        props.setCorpSecret("secret");
        assertEquals("secret", props.getCorpSecret());
    }

    @Test
    void shouldSetAndGetCorpApps() {
        DingTalkConfig props = new DingTalkConfig();
        List<DingTalkCorpAppConfig> apps = List.of(new DingTalkCorpAppConfig());
        props.setCorpApps(apps);
        assertEquals(1, props.getCorpApps().size());
    }

    @Test
    void shouldSetAndGetApps() {
        DingTalkConfig props = new DingTalkConfig();
        List<DingTalkPersonalMiniAppConfig> apps = List.of(new DingTalkPersonalMiniAppConfig());
        props.setApps(apps);
        assertEquals(1, props.getApps().size());
    }

    @Test
    void shouldSetAndGetSuites() {
        DingTalkConfig props = new DingTalkConfig();
        List<DingTalkSuiteConfig> suites = List.of(new DingTalkSuiteConfig());
        props.setSuites(suites);
        assertEquals(1, props.getSuites().size());
    }

    @Test
    void shouldSetAndGetLogins() {
        DingTalkConfig props = new DingTalkConfig();
        List<DingTalkLoginConfig> logins = List.of(new DingTalkLoginConfig());
        props.setLogins(logins);
        assertEquals(1, props.getLogins().size());
    }

    @Test
    void shouldSetAndGetRobots() {
        DingTalkConfig props = new DingTalkConfig();
        List<DingTalkRobotConfig> robots = List.of(new DingTalkRobotConfig());
        props.setRobots(robots);
        assertEquals(1, props.getRobots().size());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkConfig props = new DingTalkConfig();
        assertNull(props.getCorpId());
        assertNull(props.getCorpSecret());
        assertNull(props.getCorpApps());
        assertNull(props.getApps());
        assertNull(props.getSuites());
        assertNull(props.getLogins());
        assertNull(props.getRobots());
    }
}
