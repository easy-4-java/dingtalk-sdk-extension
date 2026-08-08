package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextMessageTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        TextMessage msg = new TextMessage();
        assertEquals(MessageType.text, msg.getMsgtype());
        assertNull(msg.getContent());
        assertNull(msg.getAtMobiles());
        assertFalse(msg.isAtAll());
    }

    @Test
    void shouldCreateWithContent() {
        TextMessage msg = new TextMessage("hello");
        assertEquals("hello", msg.getContent());
        assertEquals(MessageType.text, msg.getMsgtype());
    }

    @Test
    void shouldCreateWithContentAndAtMobiles() {
        String[] mobiles = {"13800138000", "13900139000"};
        TextMessage msg = new TextMessage("hello", mobiles);
        assertEquals("hello", msg.getContent());
        assertArrayEquals(mobiles, msg.getAtMobiles());
    }

    @Test
    void shouldCreateWithContentAndAtAll() {
        TextMessage msg = new TextMessage("hello", true);
        assertEquals("hello", msg.getContent());
        assertTrue(msg.isAtAll());
    }

    @Test
    void shouldSetAndGetContent() {
        TextMessage msg = new TextMessage();
        msg.setContent("world");
        assertEquals("world", msg.getContent());
    }

    @Test
    void shouldSetAndGetAtMobiles() {
        TextMessage msg = new TextMessage();
        String[] mobiles = {"123"};
        msg.setAtMobiles(mobiles);
        assertArrayEquals(mobiles, msg.getAtMobiles());
    }

    @Test
    void shouldSetAndGetAtAll() {
        TextMessage msg = new TextMessage();
        msg.setAtAll(true);
        assertTrue(msg.isAtAll());
    }

    @Test
    void shouldSetAndGetMsgtype() {
        TextMessage msg = new TextMessage();
        msg.setMsgtype(MessageType.text);
        assertEquals(MessageType.text, msg.getMsgtype());
    }
}
