package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkPropertiesTest {

    @Test
    void shouldHavePrefixConstant() {
        assertEquals("dingtalk", DingTalkProperties.PREFIX);
    }

    @Test
    void shouldSetAndGetCorpId() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("corp1");
        assertEquals("corp1", props.getCorpId());
    }

    @Test
    void shouldSetAndGetCorpSecret() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpSecret("secret");
        assertEquals("secret", props.getCorpSecret());
    }

    @Test
    void shouldSetAndGetCorpApps() {
        DingTalkProperties props = new DingTalkProperties();
        List<DingTalkCorpAppProperties> apps = List.of(new DingTalkCorpAppProperties());
        props.setCorpApps(apps);
        assertEquals(1, props.getCorpApps().size());
    }

    @Test
    void shouldSetAndGetApps() {
        DingTalkProperties props = new DingTalkProperties();
        List<DingTalkPersonalMiniAppProperties> apps = List.of(new DingTalkPersonalMiniAppProperties());
        props.setApps(apps);
        assertEquals(1, props.getApps().size());
    }

    @Test
    void shouldSetAndGetSuites() {
        DingTalkProperties props = new DingTalkProperties();
        List<DingTalkSuiteProperties> suites = List.of(new DingTalkSuiteProperties());
        props.setSuites(suites);
        assertEquals(1, props.getSuites().size());
    }

    @Test
    void shouldSetAndGetLogins() {
        DingTalkProperties props = new DingTalkProperties();
        List<DingTalkLoginProperties> logins = List.of(new DingTalkLoginProperties());
        props.setLogins(logins);
        assertEquals(1, props.getLogins().size());
    }

    @Test
    void shouldSetAndGetRobots() {
        DingTalkProperties props = new DingTalkProperties();
        List<DingTalkRobotProperties> robots = List.of(new DingTalkRobotProperties());
        props.setRobots(robots);
        assertEquals(1, props.getRobots().size());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkProperties props = new DingTalkProperties();
        assertNull(props.getCorpId());
        assertNull(props.getCorpSecret());
        assertNull(props.getCorpApps());
        assertNull(props.getApps());
        assertNull(props.getSuites());
        assertNull(props.getLogins());
        assertNull(props.getRobots());
    }

    @Test
    void shouldImplementToString() {
        DingTalkProperties props = new DingTalkProperties();
        props.setCorpId("c1");
        assertNotNull(props.toString());
        assertTrue(props.toString().contains("c1"));
    }
}
