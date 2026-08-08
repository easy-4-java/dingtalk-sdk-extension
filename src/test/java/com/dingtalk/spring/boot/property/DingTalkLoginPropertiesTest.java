package com.dingtalk.spring.boot.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkLoginPropertiesTest {

    @Test
    void shouldSetAndGetAppId() {
        DingTalkLoginProperties props = new DingTalkLoginProperties();
        props.setAppId("app1");
        assertEquals("app1", props.getAppId());
    }

    @Test
    void shouldSetAndGetAppSecret() {
        DingTalkLoginProperties props = new DingTalkLoginProperties();
        props.setAppSecret("secret1");
        assertEquals("secret1", props.getAppSecret());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkLoginProperties props = new DingTalkLoginProperties();
        assertNull(props.getAppId());
        assertNull(props.getAppSecret());
    }

    @Test
    void shouldImplementToString() {
        DingTalkLoginProperties props = new DingTalkLoginProperties();
        props.setAppId("a1");
        assertNotNull(props.toString());
        assertTrue(props.toString().contains("a1"));
    }
}
