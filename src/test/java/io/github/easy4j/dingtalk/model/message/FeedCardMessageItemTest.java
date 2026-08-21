package io.github.easy4j.dingtalk.model.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedCardMessageItemTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        FeedCardMessageItem item = new FeedCardMessageItem();
        assertNull(item.getTitle());
        assertNull(item.getMessageURL());
        assertNull(item.getPicURL());
    }

    @Test
    void shouldCreateWithAllFields() {
        FeedCardMessageItem item = new FeedCardMessageItem("title", "http://msg.com", "http://pic.com");
        assertEquals("title", item.getTitle());
        assertEquals("http://msg.com", item.getMessageURL());
        assertEquals("http://pic.com", item.getPicURL());
    }

    @Test
    void shouldSetAndGetTitle() {
        FeedCardMessageItem item = new FeedCardMessageItem();
        item.setTitle("new title");
        assertEquals("new title", item.getTitle());
    }

    @Test
    void shouldSetAndGetMessageURL() {
        FeedCardMessageItem item = new FeedCardMessageItem();
        item.setMessageURL("http://new.com");
        assertEquals("http://new.com", item.getMessageURL());
    }

    @Test
    void shouldSetAndGetPicURL() {
        FeedCardMessageItem item = new FeedCardMessageItem();
        item.setPicURL("http://pic.com");
        assertEquals("http://pic.com", item.getPicURL());
    }
}
