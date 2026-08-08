package com.dingtalk.spring.boot.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkPersonalMiniAppPropertiesTest {

    @Test
    void shouldSetAndGetAppId() {
        DingTalkPersonalMiniAppProperties props = new DingTalkPersonalMiniAppProperties();
        props.setAppId("app1");
        assertEquals("app1", props.getAppId());
    }

    @Test
    void shouldSetAndGetAppSecret() {
        DingTalkPersonalMiniAppProperties props = new DingTalkPersonalMiniAppProperties();
        props.setAppSecret("secret1");
        assertEquals("secret1", props.getAppSecret());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkPersonalMiniAppProperties props = new DingTalkPersonalMiniAppProperties();
        assertNull(props.getAppId());
        assertNull(props.getAppSecret());
    }

    @Test
    void shouldImplementToString() {
        DingTalkPersonalMiniAppProperties props = new DingTalkPersonalMiniAppProperties();
        props.setAppId("a1");
        assertNotNull(props.toString());
        assertTrue(props.toString().contains("a1"));
    }
}
