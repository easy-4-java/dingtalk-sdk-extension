package com.dingtalk.spring.boot.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkSuitePropertiesTest {

    @Test
    void shouldSetAndGetSuiteId() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        props.setSuiteId("s1");
        assertEquals("s1", props.getSuiteId());
    }

    @Test
    void shouldSetAndGetAppId() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        props.setAppId("a1");
        assertEquals("a1", props.getAppId());
    }

    @Test
    void shouldSetAndGetSuiteKey() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        props.setSuiteKey("k1");
        assertEquals("k1", props.getSuiteKey());
    }

    @Test
    void shouldSetAndGetSuiteSecret() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        props.setSuiteSecret("s1");
        assertEquals("s1", props.getSuiteSecret());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        assertNull(props.getSuiteId());
        assertNull(props.getAppId());
        assertNull(props.getSuiteKey());
        assertNull(props.getSuiteSecret());
    }

    @Test
    void shouldImplementToString() {
        DingTalkSuiteProperties props = new DingTalkSuiteProperties();
        props.setSuiteId("s1");
        assertNotNull(props.toString());
        assertTrue(props.toString().contains("s1"));
    }
}
