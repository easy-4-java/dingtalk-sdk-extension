package com.dingtalk.spring.boot.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkMessageTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        LinkMessage msg = new LinkMessage();
        assertEquals(MessageType.link, msg.getMsgtype());
        assertNull(msg.getTitle());
        assertNull(msg.getText());
        assertNull(msg.getMessageUrl());
        assertNull(msg.getPicUrl());
    }

    @Test
    void shouldCreateWithTitleTextAndUrl() {
        LinkMessage msg = new LinkMessage("title", "text", "http://example.com");
        assertEquals("title", msg.getTitle());
        assertEquals("text", msg.getText());
        assertEquals("http://example.com", msg.getMessageUrl());
        assertNull(msg.getPicUrl());
    }

    @Test
    void shouldCreateWithAllFields() {
        LinkMessage msg = new LinkMessage("title", "text", "http://example.com", "http://img.com/pic.png");
        assertEquals("title", msg.getTitle());
        assertEquals("text", msg.getText());
        assertEquals("http://example.com", msg.getMessageUrl());
        assertEquals("http://img.com/pic.png", msg.getPicUrl());
    }

    @Test
    void shouldSetAndGetTitle() {
        LinkMessage msg = new LinkMessage();
        msg.setTitle("new title");
        assertEquals("new title", msg.getTitle());
    }

    @Test
    void shouldSetAndGetText() {
        LinkMessage msg = new LinkMessage();
        msg.setText("new text");
        assertEquals("new text", msg.getText());
    }

    @Test
    void shouldSetAndGetMessageUrl() {
        LinkMessage msg = new LinkMessage();
        msg.setMessageUrl("http://new.com");
        assertEquals("http://new.com", msg.getMessageUrl());
    }

    @Test
    void shouldSetAndGetPicUrl() {
        LinkMessage msg = new LinkMessage();
        msg.setPicUrl("http://pic.com");
        assertEquals("http://pic.com", msg.getPicUrl());
    }
}
