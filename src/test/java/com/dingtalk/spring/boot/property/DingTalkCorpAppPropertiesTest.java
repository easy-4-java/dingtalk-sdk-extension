package com.dingtalk.spring.boot.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkCorpAppPropertiesTest {

    @Test
    void shouldSetAndGetAgentId() {
        DingTalkCorpAppProperties props = new DingTalkCorpAppProperties();
        props.setAgentId("agent1");
        assertEquals("agent1", props.getAgentId());
    }

    @Test
    void shouldSetAndGetAppKey() {
        DingTalkCorpAppProperties props = new DingTalkCorpAppProperties();
        props.setAppKey("key1");
        assertEquals("key1", props.getAppKey());
    }

    @Test
    void shouldSetAndGetAppSecret() {
        DingTalkCorpAppProperties props = new DingTalkCorpAppProperties();
        props.setAppSecret("secret1");
        assertEquals("secret1", props.getAppSecret());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkCorpAppProperties props = new DingTalkCorpAppProperties();
        assertNull(props.getAgentId());
        assertNull(props.getAppKey());
        assertNull(props.getAppSecret());
    }

    @Test
    void shouldImplementToString() {
        DingTalkCorpAppProperties props = new DingTalkCorpAppProperties();
        props.setAgentId("a1");
        assertNotNull(props.toString());
        assertTrue(props.toString().contains("a1"));
    }
}
