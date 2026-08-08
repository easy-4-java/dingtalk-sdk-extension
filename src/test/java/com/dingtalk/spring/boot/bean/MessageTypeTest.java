package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTypeTest {

    @Test
    void shouldHaveAllExpectedValues() {
        MessageType[] values = MessageType.values();
        assertEquals(5, values.length);
    }

    @Test
    void shouldContainText() {
        assertNotNull(MessageType.valueOf("text"));
        assertEquals(MessageType.text, MessageType.valueOf("text"));
    }

    @Test
    void shouldContainLink() {
        assertNotNull(MessageType.valueOf("link"));
    }

    @Test
    void shouldContainMarkdown() {
        assertNotNull(MessageType.valueOf("markdown"));
    }

    @Test
    void shouldContainActionCard() {
        assertNotNull(MessageType.valueOf("actionCard"));
    }

    @Test
    void shouldContainFeedCard() {
        assertNotNull(MessageType.valueOf("feedCard"));
    }
}
