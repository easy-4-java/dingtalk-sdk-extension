package com.dingtalk.spring.boot.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkRobotPropertiesTest {

    @Test
    void shouldSetAndGetRobotId() {
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        props.setRobotId("r1");
        assertEquals("r1", props.getRobotId());
    }

    @Test
    void shouldSetAndGetAccessToken() {
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        props.setAccessToken("token");
        assertEquals("token", props.getAccessToken());
    }

    @Test
    void shouldSetAndGetSecretToken() {
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        props.setSecretToken("secret");
        assertEquals("secret", props.getSecretToken());
    }

    @Test
    void shouldHaveNullDefaults() {
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        assertNull(props.getRobotId());
        assertNull(props.getAccessToken());
        assertNull(props.getSecretToken());
    }

    @Test
    void shouldImplementToString() {
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        props.setRobotId("r1");
        String str = props.toString();
        assertNotNull(str);
        assertTrue(str.contains("r1"));
    }
}
